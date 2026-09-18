package legend.game.unpacker.sobj;

import java.util.HashMap;
import java.util.Map;

final class ReplacementConfig {
  Map<Long, ReplacementModel> replacer = new HashMap<>();

  public Map<Long, ReplacementModel> getReplacer() {
    return this.replacer;
  }

  public void setReplacer(final Map<Long, ReplacementModel> replacer) {
    this.replacer = replacer;
  }
}
