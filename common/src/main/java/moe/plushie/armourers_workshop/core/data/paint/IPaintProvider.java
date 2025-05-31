package moe.plushie.armourers_workshop.core.data.paint;

import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;

public interface IPaintProvider {

    SkinPaintColor getColor();

    void setColor(SkinPaintColor color);
}
