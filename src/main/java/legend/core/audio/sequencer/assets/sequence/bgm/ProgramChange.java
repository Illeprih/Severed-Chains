package legend.core.audio.sequencer.assets.sequence.bgm;

import legend.core.audio.sequencer.assets.Channel;
import legend.core.audio.sequencer.assets.sequence.Command;

public final class ProgramChange implements Command {
  private final Channel channel;
  private final int programIndex;
  private final int deltaTime;

  ProgramChange(final Channel channel, final int programIndex, final int deltaTime) {
    this.channel = channel;
    this.programIndex = programIndex;
    this.deltaTime = deltaTime;
  }

  public Channel getChannel() {
    return this.channel;
  }

  public int getProgramIndex() {
    return this.programIndex;
  }

  @Override
  public int getDeltaTime() {
    return this.deltaTime;
  }

  public void apply() {
    this.channel.setProgram(this.programIndex);
    this.channel.setPitchBend(0);
    this.channel.setPriority(0x40);
  }
}
