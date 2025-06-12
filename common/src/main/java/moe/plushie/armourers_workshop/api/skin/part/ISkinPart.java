package moe.plushie.armourers_workshop.api.skin.part;

import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.api.skin.ISkinMarker;
import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometrySet;

import java.util.Collection;

public interface ISkinPart {

    /**
     * Gets the part type.
     */
    ISkinPartType type();

    /**
     * Gets the transform.
     */
    ITransform transform();

    /**
     * Gets the geometry set.
     */
    ISkinGeometrySet<?> geometries();

    /**
     * Gets the children.
     */
    Collection<? extends ISkinPart> children();

    /**
     * Gets the markers.
     */
    Collection<? extends ISkinMarker> markers();
}
