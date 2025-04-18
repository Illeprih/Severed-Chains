package legend.core.audio.sequencer.assets;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import legend.game.unpacker.FileData;

final class SoundBank {
  private final Int2ObjectArrayMap<byte[]> entries = new Int2ObjectArrayMap<>();

  SoundBank(final FileData data) {
    final IntArrayList offsets = findSounds(data);

    int lastOffset = data.size();
    for(int entry = offsets.size() - 1; entry >= 0; entry--) {
      final int offset = offsets.getInt(entry);
      final int size = lastOffset - offset;
      this.entries.put(offset, data.slice(offset, size).getBytes());
      lastOffset = offset;
    }
  }

  byte[] getEntry(final int offset) {
    return this.entries.get(offset);
  }

  private static IntArrayList findSounds(final FileData haystack) {
    final IntArrayList offsets = new IntArrayList();

    int index = 0;
    while(index <= haystack.size() - 16) {
      int matchIndex = 15;

      while(true) {
        if(haystack.readUByte(index + matchIndex) != 0) {
          index += 16;
          break;
        }

        if(matchIndex <= 0) {
          offsets.add(index);
          index += 16;
          break;
        }

        matchIndex--;
      }
    }

    return offsets;
  }
}
