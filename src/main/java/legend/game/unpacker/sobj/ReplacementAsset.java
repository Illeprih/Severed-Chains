package legend.game.unpacker.sobj;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

final class ReplacementAsset {
  @JsonDeserialize(using = HexLongDeserializer.class)
  long modelHash;
  @JsonDeserialize(using = HexLongDeserializer.class)
  long assetHash;
}
