package legend.core.audio.sequencer.assets;

import legend.game.unpacker.FileData;

import javax.annotation.Nullable;

public final class SoundFont {
  private final Program[] programs;

  SoundFont(final FileData data, final SoundBank soundBank) {
    final int instrumentsUpperBound = data.readUShort(0);

    this.programs = new Program[instrumentsUpperBound + 1];
    int lastOffset = data.size();
    for(int instrument = instrumentsUpperBound; instrument >= 0; instrument--) {
      final int offset = data.readShort(2 + instrument * 2);

      if(offset != -1) {
        this.programs[instrument] = new Program(data.slice(offset, lastOffset - offset), soundBank);

        lastOffset = offset;
      }
    }
  }

  @Nullable
  Program getInstrument(final int index) {
    if(index >= this.programs.length || index < 0) {
      return null;
    }

    return this.programs[index];
  }
}
