package moe.plushie.armourers_workshop.core.data;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

public enum OptionalDirection implements StringRepresentable {

    NONE,
    DOWN(Direction.DOWN),
    UP(Direction.UP),
    NORTH(Direction.NORTH),
    SOUTH(Direction.SOUTH),
    WEST(Direction.WEST),
    EAST(Direction.EAST);

    final Direction direction;
    final String name;

    OptionalDirection() {
        this.name = "none";
        this.direction = null;
    }

    OptionalDirection(Direction direction) {
        this.name = direction.getName();
        this.direction = direction;
    }

    public static OptionalDirection of(Direction direction) {
        for (var dir : values()) {
            if (direction.equals(dir.getDirection())) {
                return dir;
            }
        }
        return NONE;
    }

    @Nullable
    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
