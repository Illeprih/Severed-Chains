package legend.game.unpacker.sobj;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import legend.game.unpacker.FileData;

final class SobjModel {
  final FileData file;
  final Long2ObjectMap<FileData> textures = new Long2ObjectArrayMap<>();
  final Long2ObjectMap<FileData> animations = new Long2ObjectArrayMap<>();

  SobjModel(final FileData file) {
    this.file = file;
  }
}
