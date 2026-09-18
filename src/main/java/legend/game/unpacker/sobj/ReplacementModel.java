package legend.game.unpacker.sobj;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashMap;
import java.util.Map;

final class ReplacementModel {
  @JsonDeserialize(using = HexLongDeserializer.class)
  long replacement;

  Map<Long, ReplacementAsset> textures = new HashMap<>();
  Map<Long, ReplacementAsset> animations = new HashMap<>();

  public Map<Long, ReplacementAsset> getTextures() {
    return this.textures;
  }

  public void setTextures(final Map<Long, ReplacementAsset> textures) {
    this.textures = textures;
  }

  public Map<Long, ReplacementAsset> getAnimations() {
    return this.animations;
  }

  public void setAnimations(final Map<Long, ReplacementAsset> animations) {
    this.animations = animations;
  }
}
