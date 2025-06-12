package moe.plushie.armourers_workshop.api.skin.geometry;

import moe.plushie.armourers_workshop.api.core.math.ITransform3f;
import moe.plushie.armourers_workshop.api.core.math.IVoxelShape;

public interface ISkinGeometry {

    /**
     * Gets the geometry type.
     */
    ISkinGeometryType type();

    /**
     * Gets the geometry options.
     */
    ISkinGeometryOptions options();

    /**
     * Gets the geometry transform.
     */
    ITransform3f transform();

    /**
     * Gets the geometry shape.
     */
    IVoxelShape shape();

    /**
     * Gets the geometry all faces.
     */
    Iterable<? extends ISkinGeometryFace> faces();
}
