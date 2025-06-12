package moe.plushie.armourers_workshop.api.skin.geometry;

import moe.plushie.armourers_workshop.api.core.math.IVector2f;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.api.skin.texture.ISkinPaintColor;

public interface ISkinGeometryVertex {

    int id();

    IVector3f position();

    IVector3f normal();

    IVector2f textureCoords();

    ISkinPaintColor color();
}
