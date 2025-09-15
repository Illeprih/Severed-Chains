package legend.core.audio;

public final class Constants {
  private static final int BASE_SAMPLE_RATE = 44_100;
  public static final int ENGINE_SAMPLE_RATE = 48_000;
  public static final double SAMPLE_RATE_RATIO = BASE_SAMPLE_RATE / (double)ENGINE_SAMPLE_RATE;

  public static final int BASE_TICKS_PER_SECOND = 60;
  public static final int BUFFERS_PER_TICK = 4;
  public static final int BUFFER_SIZE = ENGINE_SAMPLE_RATE * 2 / (BASE_TICKS_PER_SECOND * BUFFERS_PER_TICK);

  /**
   * 26 - The largest safe value
   * <p>
   * The sample buffer has 28 samples. Each octave shift doubles the frequency,
   * meaning that at a 4-octave shift, only every 16th sample would be played,
   * making the theoretical limit of the retail engine ~4 octaves and 9,5 semitones.
   * A bit shift of 26 supports ~4 octaves and 3,5 semitones, however shifts that high
   * are entirely impractical, so it should cover the entire effective range.
   *
   * <p>
   * 27 - The retail engine limit
   * <p>
   * While a shift of 27 works, it introduces risk of overflowing the voice counter,
   * if the limits of the retail engine were to be exceeded. The retail engine
   * caps the pitch shift at 2 octaves; bit shift of 27 allows for pitch shift
   * of ~2 octaves and 1 and a quarter semitone:
   * <pre>
   *   (28 << 27) - 1 + (1 << 29) * (44_100 / 48_000) * 2<sup>(5/48)</sup> ≈ 0xFF99_ED3C
   * </pre>
   * <p>
   * Another option would be to use a long for the counter and use all the bits for every pitch value.
   * That would make 31 possible (entire octave is doubling of frequency, so it's guaranteed to fit).
   */
  public static final int PITCH_BIT_SHIFT = 27;

  /** There are originally 60 values representing 1 second of breath control at the lowest speed */
  private static final int BASE_BREATH_COUNT = 60;

  /** The sweet spot for these seems to be between 240 and 480,
   * since some presets have double frequency (4π), it seems better to aim for 480 (<< 3).
   * The Catmull-Rom splines are pretty good at interpolating these, but if you consider 480 to be base,
   * then 120 will produce results that are on average ~10x closer and with ~8x smaller maximum deviation,
   * 240 will produce results that are on average ~2100x closer and with ~1600x smaller maximum deviation.
   * There are so few base values, that the increase in memory usage ends up being so minimal,
   * that it seems worth it, even if the difference might be difficult to perceive.
   * Even at 60, the deviation becomes so tiny, that it's way below the 128th of a semitone resolution,
   * but it can reduce off by one errors.
   */
  private static final int BREATH_COUNT_SHIFT = 3;

  public static final int BREATH_COUNT = BASE_BREATH_COUNT << BREATH_COUNT_SHIFT;

  /** The maximum speed for traversing the breath wave is x30.
   * <pre>
   *   (60 << 25) - 1 + (30 << 25) = 0xB3FF_FFFF
   * </pre>
   * Any more than 25 and you could overflow the counter.
   * <p>
   * However, something interesting happens when you try to process at least 480 times a second (x8 the retail rate).
   * On every tick, you'd have to add 1/8th of the value.
   * <pre>
   *   (60 << 26) - 1 + (30 << 23) = 0xFEFF_FFFF
   * </pre>
   * So when going from a x4 to x8, instead of halving the amount, we can double the max value.
   */
  private static final int BASE_BREATH_BIT_SHIFT = 25;
  public static final float BREATH_FORMAT_MULTIPLIER = 1.0f / (1 << BASE_BREATH_BIT_SHIFT);
  public static final int BREATH_BIT_SHIFT = BASE_BREATH_BIT_SHIFT - BREATH_COUNT_SHIFT;
  public static final int BREATH_MAX_VALUE = BASE_BREATH_COUNT << BASE_BREATH_BIT_SHIFT;

  public static final int INTERP_TAPS = 24;


  private Constants() {}
}
