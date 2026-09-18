package legend.game.unpacker.sobj;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

final class HexLongDeserializer extends JsonDeserializer<Long> {
  @Override
  public Long deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
    final String text = parser.getText().trim();

    if(text.startsWith("0x") || text.startsWith("0X")) {
      return Long.parseUnsignedLong(text.substring(2), 16);
    }

    return Long.parseUnsignedLong(text, 16);
  }
}