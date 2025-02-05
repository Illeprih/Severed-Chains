package legend.core.audio.sequencer.assets.sequence.sfx;

import legend.core.audio.sequencer.assets.Channel;
import legend.core.audio.sequencer.assets.sequence.Command;

public final class BreathChange implements Command {
  private final Channel channel;
  private final int breath;
  private final int note;
  private final int deltaTime;

  BreathChange(final int breath, final Channel channel, final int note, final int deltaTime) {
    this.channel = channel;
    this.breath = breath;
    this.note = note;
    this.deltaTime = deltaTime;
  }

  public Channel getChannel() {
    return this.channel;
  }

  public int getBreath() {
    return this.breath;
  }

  public int getNote() {
    return this.note;
  }

  @Override
  public int getDeltaTime() {
    return this.deltaTime;
  }
}
