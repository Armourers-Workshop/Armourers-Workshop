package moe.plushie.armourers_workshop.api.skin.geometry;

import moe.plushie.armourers_workshop.api.core.math.ITransform3f;
import moe.plushie.armourers_workshop.api.core.math.IVoxelShape;

public interface ISkinGeometry {

    /**
     * Gets the geometry type.
     */
    ISkinGeometryType getType();

    /**
     * Gets the geometry options.
     */
    ISkinGeometryOptions getOptions();

    /**
     * Gets the geometry transform.
     */
    ITransform3f getTransform();

    /**
     * Gets the geometry shape.
     */
    IVoxelShape getShape();

    /**
     * Gets the geometry all faces.
     */
    Iterable<? extends ISkinGeometryFace> getFaces();
}
