package legend.game.unpacker;

import java.nio.charset.StandardCharsets;

public class FileMap {
  private final StringBuilder sb = new StringBuilder();

  public void addFile(final String name, final String target, final int size) {
    this.sb.append(name).append('=').append(target).append(';').append(size).append('\n');
  }

  public void addFile(final String name, final int size) {
    this.addFile(name, name, size);
  }

  public void addFile(final int index, final boolean virtual, final int target, final int size) {
    this.sb.append(index).append('=');

    if(virtual) {
      this.sb.append('@');
    }

    this.sb.append(target).append(';').append(size).append('\n');
  }

  public FileData build() {
    return new FileData(this.sb.toString().getBytes(StandardCharsets.US_ASCII));
  }
}
