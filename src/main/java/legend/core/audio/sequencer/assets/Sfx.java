package legend.core.audio.sequencer.assets;

import legend.core.audio.SampleRate;
import legend.core.audio.sequencer.assets.sequence.Command;
import legend.core.audio.sequencer.assets.sequence.sfx.SequenceBuilder;
import legend.game.unpacker.FileData;

import java.util.List;

public final class Sfx {
  private float volume;

  private final short[][] breathControls;
  private final byte[] velocityRamp = new byte[0x80];
  private final Channel[] channels = new Channel[24];
  private final Command[][][] sequences;
  private final SoundFont soundFont;

  public Sfx(final List<FileData> files) {
    final FileData sshd = files.get(2);

    final int[] subfileOffsets = new int[5];

    for(int offset = 0; offset < subfileOffsets.length; offset++) {
      subfileOffsets[offset] = sshd.readInt(16 + offset * 4);
    }

    sshd.copyFrom(subfileOffsets[1] + 2, this.velocityRamp, 0, 0x80);

    final SoundBank soundBank = new SoundBank(files.get(3));
    this.soundFont = new SoundFont(sshd.slice(subfileOffsets[4] + 0x190), soundBank, SampleRate._48000);

    this.volume = sshd.readUByte(subfileOffsets[4]) / 128.0f;

    for(int channel = 0; channel < this.channels.length; channel++) {
      this.channels[channel] = new Channel(sshd.slice(subfileOffsets[4] + 16 + channel * 16, 16), this.volume, this.soundFont);
    }

    this.sequences = SequenceBuilder.process(sshd.slice(subfileOffsets[3]), this.channels);

    if(subfileOffsets[2] == -1) {
      this.breathControls = new short[0][];
    } else {
      this.breathControls = new short[sshd.readUByte(subfileOffsets[2] + 1)][];
    }

    for(int i = 0; i < this.breathControls.length; i++) {
      final int relativeOffset = sshd.readShort(2 + i * 2 + subfileOffsets[2]);

      if(relativeOffset != -1) {
        this.breathControls[i] = new short[63];
        final int startingPosition = subfileOffsets[2] + relativeOffset;
        for(int b = 0; b < 60; b++) {
          this.breathControls[i][b + 1] = (short)(sshd.readUByte(startingPosition + b) - 0x80);
        }

        this.breathControls[i][0] = this.breathControls[i][59];
        this.breathControls[i][61] = this.breathControls[i][2];
        this.breathControls[i][62] = this.breathControls[i][3];
      }
    }
  }
}
