package moe.plushie.armourers_workshop.core.skin.geometry;

import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryFace;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;
import moe.plushie.armourers_workshop.core.utils.Objects;

public abstract class SkinGeometryFace implements ISkinGeometryFace {

    protected int id;

    protected OpenTransform3f transform = OpenTransform3f.IDENTITY;
    protected SkinTexturePos texturePos = SkinTexturePos.DEFAULT;

    @Override
    public int id() {
        return id;
    }

    @Override
    public OpenTransform3f transform() {
        return transform;
    }

    @Override
    public SkinTexturePos texturePos() {
        return texturePos;
    }

    @Override
    public abstract SkinGeometryType type();

    @Override
    public abstract SkinGeometryOptions options();

    @Override
    public abstract Iterable<? extends SkinGeometryVertex> vertices();

    public float priority() {
        return 0;
    }

    public boolean isVisible() {
        return true;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "id", id(), "type", type());
    }
}
