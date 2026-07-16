package legend.core.audio;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import static org.lwjgl.openal.AL10.AL_BUFFERS_PROCESSED;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceQueueBuffers;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourceUnqueueBuffers;
import static org.lwjgl.openal.AL10.alSourcef;

public abstract class AudioSource {
  private final AudioTag tag;
  private final int[] buffers = new int[6];
  private int bufferIndex;
  private int sourceId;
  private final int format;
  private final int sampleRate;
  protected boolean eof;

  private boolean active;

  public AudioSource(final int format, final int sampleRate, final float volume, final AudioTag tag) {
    this.tag = tag;
    this.format = format;
    this.sampleRate = sampleRate;

    this.sourceId = alGenSources();
    alGenBuffers(this.buffers);
    this.bufferIndex = this.buffers.length - 1;

    this.setVolume(volume);
  }

  public AudioTag getTag() {
    return this.tag;
  }

  protected boolean isInitialized() {
    return this.sourceId != 0;
  }

  protected void init() {
    this.sourceId = alGenSources();

    alGenBuffers(this.buffers);
    this.bufferIndex = this.buffers.length - 1;
  }

  protected boolean isFinished() {
    return (this.eof && this.bufferIndex == this.buffers.length - 1);
  }

  protected void destroy() {
    this.active = false;
    alSourceStop(this.sourceId);


    final int processedBufferCount = alGetSourcei(this.sourceId, AL_BUFFERS_PROCESSED);

    for(int buffer = 0; buffer < processedBufferCount; buffer++) {
      final int processedBufferName = alSourceUnqueueBuffers(this.sourceId);
      alDeleteBuffers(processedBufferName);
    }

    alDeleteBuffers(this.buffers);
    alDeleteSources(this.sourceId);

    Arrays.fill(this.buffers, 0);
    this.sourceId = 0;
  }

  public void tick() {
    this.handleProcessedBuffers();

    // Wait until we have only two buffers length
    if(!this.eof && this.bufferIndex >= this.buffers.length - 3) {
      while(this.bufferIndex >= 0) {
        if(this.eof) {
          break;
        }

        this.fillBuffer();
      }
    }

    // Restart playback if stopped
    if(this.isActive()) {
      this.play();
    }
  }

  /** This method should handle the processing and invoke the bufferOutput */
  protected abstract void fillBuffer();

  public boolean canBuffer() {
    if(!this.active || !this.isInitialized()) {
      return false;
    }

    return this.bufferIndex >= 2;
  }

  protected void handleProcessedBuffers() {
    if(this.bufferIndex < this.buffers.length - 1) {
      final int processedBufferCount = alGetSourcei(this.sourceId, AL_BUFFERS_PROCESSED);

      for(int buffer = 0; buffer < processedBufferCount; buffer++) {
        this.buffers[++this.bufferIndex] = alSourceUnqueueBuffers(this.sourceId);
      }
    }
  }

  protected void bufferOutput(final ByteBuffer buffer) {
    synchronized(this) {
      if(this.bufferIndex >= 0) {
        final int bufferId = this.buffers[this.bufferIndex--];
        alBufferData(bufferId, this.format, buffer, this.sampleRate);
        alSourceQueueBuffers(this.sourceId, bufferId);
      }
    }
  }

  protected void bufferOutput(final short[] buffer) {
    synchronized(this) {
      if(this.bufferIndex >= 0) {
        final int bufferId = this.buffers[this.bufferIndex--];
        alBufferData(bufferId, this.format, buffer, this.sampleRate);
        alSourceQueueBuffers(this.sourceId, bufferId);
      }
    }
  }

  protected void bufferOutput(final ShortBuffer buffer) {
    synchronized(this) {
      if(this.bufferIndex >= 0) {
        final int bufferId = this.buffers[this.bufferIndex--];
        alBufferData(bufferId, this.format, buffer, this.sampleRate);
        alSourceQueueBuffers(this.sourceId, bufferId);
      }
    }
  }

  protected void bufferOutput(final float[] buffer) {
    synchronized(this) {
      if(this.bufferIndex >= 0) {
        final int bufferId = this.buffers[this.bufferIndex--];
        alBufferData(bufferId, this.format, buffer, this.sampleRate);
        alSourceQueueBuffers(this.sourceId, bufferId);
      }
    }
  }

  protected void play() {
    if(alGetSourcei(this.sourceId, AL_SOURCE_STATE) != AL_PLAYING) {
      alSourcePlay(this.sourceId);
    }
  }

  protected void stop() {
    this.active = false;

    if(this.isInitialized()) {
      alSourceStop(this.sourceId);
    }
  }

  protected void setActive(final boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return this.active;
  }

  public void setVolume(final float volume) {
    alSourcef(this.sourceId, AL_GAIN, volume);
  }
}
