package legend.game.unpacker.sobj;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

final class HexLongKeyDeserializer extends KeyDeserializer {
  @Override
  public Object deserializeKey(final String key, final DeserializationContext context) {
    if (key.startsWith("0x") || key.startsWith("0X")) {
      return Long.parseUnsignedLong(key.substring(2), 16);
    }

    return Long.parseUnsignedLong(key, 16);
  }
}