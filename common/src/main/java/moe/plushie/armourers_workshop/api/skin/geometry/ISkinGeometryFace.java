package moe.plushie.armourers_workshop.api.skin.geometry;

import moe.plushie.armourers_workshop.api.core.math.ITransform3f;
import moe.plushie.armourers_workshop.api.skin.texture.ISkinTexturePos;

public interface ISkinGeometryFace {

    /**
     * Gets the geometry face id.
     */
    int id();

    /**
     * Gets the geometry type.
     */
    ISkinGeometryType type();

    /**
     * Gets the geometry options;
     */
    ISkinGeometryOptions options();

    /**
     * Gets the face transform.
     */
    ITransform3f transform();

    /**
     * Get the face used texture key.
     */
    ISkinTexturePos texturePos();

    /**
     * Gets the all vertices of the face.
     */
    Iterable<? extends ISkinGeometryVertex> vertices();
}
