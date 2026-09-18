package legend.game.unpacker.sobj;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import legend.game.unpacker.FileData;
import legend.game.unpacker.PathNode;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

final class RefArchive {
  private static final Pattern MRG_ENTRY = Pattern.compile("[=;]");

  final Int2ObjectMap<Ref> files = new Int2ObjectArrayMap<>();

  RefArchive(final PathNode mrg) {
    final String mrgContents = mrg.data.readFixedLengthAscii(0x0, mrg.data.size());

    mrgContents.lines().forEach(line -> {
      final String[] parts = MRG_ENTRY.split(line);

      if(parts.length != 3) {
        throw new IllegalArgumentException("Invalid MRG entry: " + line);
      }

      final int index = Integer.parseInt(parts[0]);

      // Indicates no file. Probably not used
      if(parts[1].isBlank()) {
        return;
      }

      if(parts[1].startsWith("@")) {
        this.files.put(index, new Ref(true, parts[1].substring(1), Integer.parseInt(parts[2])));
        return;
      }

      final boolean virtual = !parts[0].equals(parts[1]);

      this.files.put(index, new Ref(virtual, parts[1], Integer.parseInt(parts[2])));
    });
  }

  int[] parseModels() {
    final int modelCount = this.files.size() / 33;

    final int[] modelMap = new int[modelCount];

    for(int i = 0; i < modelCount; i++) {
      modelMap[i] = Integer.parseInt(this.files.get(i * 33).ref) / 33;
    }

    return modelMap;
  }

  FileData toFileData() {
    final StringBuilder sb = new StringBuilder();

    for(final var entry : this.files.int2ObjectEntrySet()) {
      final int key = entry.getIntKey();
      final Ref ref = entry.getValue();

      final String r = ref.virtual ? '@' + ref.ref : ref.ref;

      sb.append(key).append('=').append(r).append(';').append(ref.size).append('\n');
    }

    return new FileData(sb.toString().getBytes(StandardCharsets.US_ASCII));
  }

  static class Ref {
    boolean virtual;
    String ref;
    int size;

    private Ref(final boolean virtual, final String ref, final int size) {
      this.virtual = virtual;
      this.ref = ref;
      this.size = size;
    }
  }
}
