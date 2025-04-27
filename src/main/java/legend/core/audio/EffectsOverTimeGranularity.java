package legend.core.audio;

import static legend.core.audio.Constants.BREATH_BIT_SHIFT;
import static legend.core.audio.Constants.BREATH_MAX_VALUE;

public enum EffectsOverTimeGranularity {
  Quadruple(0,2),
  Octuple(1, 2),
  Sexdecuple(1, 3);

  public final int BreathBase;
  public final int BreathBitShift;
  public final int BreathAmountShift;

  EffectsOverTimeGranularity( final int extraShift, final int breathAmountShift) {
    this.BreathBase = BREATH_MAX_VALUE << extraShift;
    this.BreathBitShift = BREATH_BIT_SHIFT + extraShift;
    this.BreathAmountShift = breathAmountShift;
  }
}
