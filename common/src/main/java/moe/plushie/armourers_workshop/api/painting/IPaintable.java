package moe.plushie.armourers_workshop.api.painting;

import net.minecraft.core.Direction;

import java.util.Map;

public interface IPaintable {

    IPaintColor getColor(Direction direction);

    void setColor(Direction direction, IPaintColor color);

    default void setColors(Map<Direction, IPaintColor> colors) {
        colors.forEach(this::setColor);
    }

    default boolean shouldChangeColor(Direction direction) {
        return true;
    }
}
