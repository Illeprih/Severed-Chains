package legend.core.opengl;

import legend.game.types.Translucency;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

public class MeshObj extends Obj {
  private final Mesh[] meshes;
  private final boolean backfaceCulling;
  private final boolean textured;
  private final boolean opaque;
  private final boolean translucent;
  protected final Set<Translucency> translucencies = EnumSet.noneOf(Translucency.class);

  public MeshObj(final String name, final Mesh[] meshes) {
    this(name, meshes, true);
  }

  public MeshObj(final String name, final Mesh[] meshes, final boolean backfaceCulling) {
    super(name);
    this.meshes = meshes;
    this.backfaceCulling = backfaceCulling;

    boolean textured = false;
    boolean opaque = false;
    boolean translucent = false;
    for(int i = 0; i < meshes.length; i++) {
      if(meshes[i].translucent) {
        if(meshes[i].translucencyMode != null) {
          this.translucencies.add(meshes[i].translucencyMode);
        }

        translucent = true;
      } else {
        opaque = true;
      }

      if(meshes[i].textured) {
        textured = true;
      }
    }

    this.textured = textured;
    this.opaque = opaque;
    this.translucent = translucent;
  }

  @Override
  public boolean useBackfaceCulling() {
    return this.backfaceCulling;
  }

  @Override
  public boolean hasTexture() {
    return this.textured;
  }

  @Override
  public boolean hasTexture(final int index) {
    return this.meshes[index].textured;
  }

  @Override
  public boolean hasTranslucency() {
    return this.translucent;
  }

  @Override
  public boolean hasTranslucency(final int index) {
    return this.meshes[index].translucent;
  }

  @Override
  public boolean shouldRender(@Nullable final Translucency translucency) {
    if(translucency == null) {
      return this.opaque;
    }

    return this.translucencies.contains(translucency);
  }

  @Override
  public boolean shouldRender(@Nullable final Translucency translucency, final int layer) {
    return this.meshes[layer].translucencyMode == translucency;
  }

  @Override
  public int getLayers() {
    return this.meshes.length;
  }

  @Override
  public void render(final int layer, final int startVertex, final int vertexCount) {
    this.meshes[layer].draw(startVertex, vertexCount);
  }

  @Override
  public void render(@Nullable final Translucency translucency, final int layer, final int startVertex, final int vertexCount) {
    final Mesh mesh = this.meshes[layer];

    if(!mesh.translucent && translucency == null || mesh.translucent && mesh.translucencyMode == translucency) {
      mesh.draw(startVertex, vertexCount);
    }
  }

  @Override
  protected void performDelete() {
    for(final Mesh mesh : this.meshes) {
      mesh.delete();
    }
  }
}
