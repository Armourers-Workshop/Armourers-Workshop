package moe.plushie.armourers_workshop.api.painting;

import net.minecraft.util.Direction;

public interface IBlockPaintColor {

    boolean isEmpty();

    boolean isPureColor();

    void put(Direction dir, IPaintColor color);

    IPaintColor get(Direction dir);

    default IPaintColor getOrDefault(Direction dir, IPaintColor defaultValue) {
        IPaintColor paintColor = get(dir);
        if (paintColor == null) {
            paintColor = defaultValue;
        }
        return paintColor;
    }
}
