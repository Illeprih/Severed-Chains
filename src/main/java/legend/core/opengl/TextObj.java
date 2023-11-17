package legend.core.opengl;

import legend.game.types.Translucency;

import javax.annotation.Nullable;

public class TextObj extends Obj {
  private final Mesh mesh;

  public TextObj(final String name, final Mesh mesh) {
    super(name);
    this.mesh = mesh;
  }

  @Override
  public boolean shouldRender(@Nullable final Translucency translucency) {
    return !this.deleted && translucency == null;
  }

  @Override
  public void render(@Nullable final Translucency translucency) {
    this.mesh.draw();
  }

  @Override
  public void delete() {
    super.delete();
    this.mesh.delete();
  }
}