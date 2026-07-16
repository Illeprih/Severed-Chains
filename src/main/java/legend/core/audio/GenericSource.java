package legend.core.audio;

import java.nio.ByteBuffer;

public class GenericSource extends AudioSource {

  public GenericSource(final int format, final int sampleRate) {
    super(format, sampleRate, 1, AudioTag.Generic);
  }

  @Override
  protected void fillBuffer() {

  }

  public void bufferOutput(final ByteBuffer buffer) {
    this.setActive(true);
    super.bufferOutput(buffer);
  }

  public void bufferOutput(final short[] buffer) {
    this.setActive(true);
    super.bufferOutput(buffer);
  }
}
