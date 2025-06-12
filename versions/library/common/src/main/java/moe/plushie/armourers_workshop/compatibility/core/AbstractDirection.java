package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.utils.EnumMapper;
import net.minecraft.core.Direction;

@Available("[1.16, )")
public class AbstractDirection {

    private static final EnumMapper<OpenDirection, Direction> MAPPER = EnumMapper.create(OpenDirection.NORTH, Direction.NORTH, it -> {
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
}
