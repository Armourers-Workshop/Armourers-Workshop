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
    public int getId() {
        return id;
    }

    @Override
    public OpenTransform3f getTransform() {
        return transform;
    }

    @Override
    public SkinTexturePos getTexturePos() {
        return texturePos;
    }

    @Override
    public abstract SkinGeometryType getType();

    @Override
    public abstract SkinGeometryOptions getOptions();

    @Override
    public abstract Iterable<? extends SkinGeometryVertex> getVertices();

    public float getPriority() {
        return 0;
    }

    public boolean isVisible() {
        return true;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "id", getId(), "type", getType());
    }
}
