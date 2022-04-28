package moe.plushie.armourers_workshop.api.painting;

import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.Map;

public interface IPaintable {

    IPaintColor getColor(Direction direction);

    void setColor(Direction direction, IPaintColor color);

    default void setColors(Map<Direction, IPaintColor> colors) {
        colors.forEach(this::setColor);
    }
}
