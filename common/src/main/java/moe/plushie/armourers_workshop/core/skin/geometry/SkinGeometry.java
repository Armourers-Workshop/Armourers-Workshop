package moe.plushie.armourers_workshop.core.skin.geometry;

import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometry;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;

public abstract class SkinGeometry implements ISkinGeometry {

    protected SkinGeometryOptions options = SkinGeometryOptions.EMPTY;
    protected OpenTransform3f transform = OpenTransform3f.IDENTITY;

    @Override
    public OpenTransform3f transform() {
        return transform;
    }

    @Override
    public abstract OpenVoxelShape shape();

    @Override
    public abstract SkinGeometryType type();

    @Override
    public SkinGeometryOptions options() {
        return options;
    }

    @Override
    public Iterable<? extends SkinGeometryFace> faces() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return Objects.toString(this, "type", type(), "shape", shape().bounds());
    }
}
