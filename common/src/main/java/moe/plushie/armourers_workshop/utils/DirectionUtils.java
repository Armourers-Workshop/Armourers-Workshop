package moe.plushie.armourers_workshop.utils;

import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class DirectionUtils {

    private static final ConcurrentHashMap<Integer, Collection<Direction>> SET_TO_VALUES = new ConcurrentHashMap<>();

    public static Collection<Direction> valuesFromSet(int set) {
        // 0x3f => 0011 1111
        return SET_TO_VALUES.computeIfAbsent(set & 0x3f, it -> {
            ArrayList<Direction> dirs = new ArrayList<>();
            for (Direction dir : Direction.values()) {
                if ((it & (1 << dir.get3DDataValue())) != 0) {
                    dirs.add(dir);
                }
            }
            return dirs;
        });
    }

}
