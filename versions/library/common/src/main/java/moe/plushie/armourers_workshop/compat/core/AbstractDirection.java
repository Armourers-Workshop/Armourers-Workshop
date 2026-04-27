package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.utils.IDirection;
import moe.plushie.armourers_workshop.core.utils.FastMapper;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.core.Direction;

@Available("[16, )")
public class AbstractDirection {

    private static final FastMapper<OpenDirection, Direction> MAPPER = FastMapper.builder(OpenDirection.NORTH, Direction.NORTH, it -> {
        it.put(OpenDirection.DOWN, Direction.DOWN);
        it.put(OpenDirection.UP, Direction.UP);
        it.put(OpenDirection.NORTH, Direction.NORTH);
        it.put(OpenDirection.SOUTH, Direction.SOUTH);
        it.put(OpenDirection.WEST, Direction.WEST);
        it.put(OpenDirection.EAST, Direction.EAST);
    });

    public static OpenDirection wrap(Direction direction) {
        return MAPPER.getKey(direction);
    }

    public static Direction unwrap(OpenDirection direction) {
        return MAPPER.getValue(direction);
    }

    public static Direction unwrap(IDirection direction) {
        return unwrap((OpenDirection) direction);
    }
}
