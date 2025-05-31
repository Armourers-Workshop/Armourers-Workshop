package moe.plushie.armourers_workshop.core.data.paint;

import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.util.Map;

public interface IBlockPaintable {

    SkinPaintColor getColor(OpenDirection direction);

    void setColor(OpenDirection direction, SkinPaintColor color);

    default void setColors(Map<OpenDirection, SkinPaintColor> colors) {
        colors.forEach(this::setColor);
    }

    default boolean hasColor(OpenDirection direction) {
        return true;
    }

    default boolean shouldChangeColor(OpenDirection direction) {
        return true;
    }
}
