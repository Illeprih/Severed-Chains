package legend.core.audio.sequencer.assets;

import legend.game.unpacker.FileData;

import java.util.ArrayList;
import java.util.List;

public final class Program {
  private final Type type;
  private final Tone[] tones;

  private final float volume;
  private final int pan;
  // 0x03 unused
  private final int pitchBendMultiplier;
  private final int breathControlIndex;
  /** For SFX only */
  private final int startingNote;

  Program(final FileData data, final SoundBank soundBank) {
    final int upperBoundByte = data.readUByte(0x00);
    this.type = Type.getType(upperBoundByte);

    this.volume = data.readUByte(0x01) / 128.0f;
    this.pan = data.readUByte(0x02);
    // 0x03 unused
    this.pitchBendMultiplier = data.readUByte(0x04);
    this.breathControlIndex = data.readUByte(0x05);
    this.startingNote = data.readUByte(0x06);

    final int toneUpperBound = this.type == Type.SFX ? data.readUByte(0x07) : upperBoundByte & 0x7f;
    this.tones = new Tone[toneUpperBound + 1];
    for(int tone = 0; tone < this.tones.length; tone++) {
      this.tones[tone] = new Tone(data.slice(8 + tone * 16, 16), soundBank);
    }
  }

  public List<Tone> getTones(final int note) {
    final List<Tone> tones = new ArrayList<>();

    if(this.type == Type.SFX) {
      tones.add(this.tones[note - this.startingNote]);
      return tones;
    }

    for(final Tone layer : this.tones) {
      if(layer.canPlayNote(note)) {
        tones.add(layer);

        if(this.type == Type.STANDARD) {
          return tones;
        }
      }
    }

    return tones;
  }

  public float getVolume() {
    return this.volume;
  }

  public int getPan() {
    return this.pan;
  }

  public int getPitchBendMultiplier() {
    return this.pitchBendMultiplier;
  }

  public int getBreathControlIndex() {
    return this.breathControlIndex;
  }

  private enum Type {
    STANDARD,
    MULTI_TONE,
    SFX;

    private static Type getType(final int upperBoundByte) {
      if(upperBoundByte == 0xff) {
        return Type.SFX;
      }

      if((upperBoundByte & 0x80) != 0) {
        return Type.MULTI_TONE;
      }

      return Type.STANDARD;
    }
  }
}
