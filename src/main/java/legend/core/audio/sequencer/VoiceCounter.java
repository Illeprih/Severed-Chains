package legend.core.audio.sequencer;

import legend.core.audio.Constants;
import legend.core.audio.EffectsOverTimeGranularity;
import legend.core.audio.InterpolationPrecision;

import static legend.core.audio.Constants.BREATH_COUNT;
import static legend.core.audio.Constants.PITCH_BIT_SHIFT;

final class VoiceCounter {
  //TODO verify this is actually correct for other values in case we want to change
  //     the window size. This should be a generic solution but it wasn't verified.
  private final static int START_OFFSET = ((Voice.EMPTY.length) / 2 + 1) << PITCH_BIT_SHIFT;
  private static final int SAMPLE_MAX_VALUE = 28 << PITCH_BIT_SHIFT;

  private int sampleCounter = START_OFFSET;
  private int breathCounter;

  private InterpolationPrecision precision;
  private EffectsOverTimeGranularity granularity;
  private int breathInterpolationShift;

  VoiceCounter(final InterpolationPrecision precision, final EffectsOverTimeGranularity granularity) {
    this.precision = precision;
    this.granularity = granularity;

    this.breathInterpolationShift = granularity.BreathBitShift - precision.value;
  }

  int getCurrentSampleIndex() {
    return this.sampleCounter >>> PITCH_BIT_SHIFT;
  }

  int getSampleInterpolationIndex() {
    return this.sampleCounter >>> this.precision.sampleInterpolationShift & this.precision.interpolationAnd;
  }

  /** Adds value to the counter, returns true if the end of block was reached */
  boolean add(final int value) {
    this.sampleCounter += value;

    final int sampleIndex = this.getCurrentSampleIndex();
    if(sampleIndex >= 28) {
      this.sampleCounter -= SAMPLE_MAX_VALUE;
      return true;
    }

    return false;
  }

  int getCurrentBreathIndex() {
    return this.breathCounter >>> this.granularity.BreathBitShift;
  }

  int getCurrentBreathDoubleIndex() {
    return (this.breathCounter >>> (this.granularity.BreathBitShift - 1)) & BREATH_COUNT;
  }

  int getBreathInterpolationIndex() {
    return (this.breathCounter >>> this.breathInterpolationShift) & this.precision.interpolationAnd;
  }

  int getBreathDoubleInterpolationIndex() {
    return (this.breathCounter >>> (this.breathInterpolationShift - 1)) & this.precision.interpolationAnd;
  }

  void addBreath(final int breath) {
    this.breathCounter += breath >>> this.granularity.BreathAmountShift;

    if(this.getCurrentBreathIndex() >= BREATH_COUNT) {
      this.breathCounter -= this.granularity.BreathBase;
    }
  }

  void reset() {
    this.sampleCounter = START_OFFSET;
    this.resetBreath();
  }

  void resetBreath() {
    this.breathCounter = 0;
  }

  void changeInterpolationPrecision(final InterpolationPrecision precision) {
    this.precision = precision;
  }

  void changeGranularity(final EffectsOverTimeGranularity granularity) {
    // Do the shift from 25 to 26 bits for that little extra quality
    if(this.granularity.BreathBase != granularity.BreathBase) {
      if(this.granularity == EffectsOverTimeGranularity.Sexdecuple || this.granularity == EffectsOverTimeGranularity.Octuple) {
        this.breathCounter >>>= 1;
      } else {
        this.breathCounter <<= 1;
      }
    }

    this.granularity = granularity;
    this.breathInterpolationShift = granularity.BreathBitShift - this.precision.value;
  }
}
