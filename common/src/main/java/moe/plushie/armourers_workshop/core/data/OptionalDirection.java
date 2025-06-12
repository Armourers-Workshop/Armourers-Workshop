package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.compatibility.core.AbstractDirection;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public enum OptionalDirection {

    NONE,
    DOWN(OpenDirection.DOWN),
    UP(OpenDirection.UP),
    NORTH(OpenDirection.NORTH),
    SOUTH(OpenDirection.SOUTH),
    WEST(OpenDirection.WEST),
    EAST(OpenDirection.EAST);

    final OpenDirection direction;
    final String name;

    OptionalDirection() {
        this.name = "none";
        this.direction = null;
    }

    OptionalDirection(OpenDirection direction) {
        this.name = direction.serializedName();
        this.direction = direction;
    }

    public static OptionalDirection of(OpenDirection direction) {
        for (var dir : values()) {
            if (direction.equals(dir.direction())) {
                return dir;
            }
        }
        return NONE;
    }

    public static OptionalDirection of(Direction direction) {
        return of(AbstractDirection.wrap(direction));
    }

    @Nullable
    public OpenDirection direction() {
        return direction;
    }

    @Override
    public String toString() {
        return name;
    }
}
