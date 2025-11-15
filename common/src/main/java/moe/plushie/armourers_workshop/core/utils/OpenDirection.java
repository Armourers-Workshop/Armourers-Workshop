package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.utils.IDirection;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum OpenDirection implements IDirection {

    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new OpenVector3i(0, -1, 0)),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new OpenVector3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new OpenVector3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new OpenVector3i(0, 0, 1)),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new OpenVector3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new OpenVector3i(1, 0, 0));

    private final int data3d;
    private final int oppositeIndex;
    private final int data2d;
    private final String name;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final OpenVector3i normal;
    private static final OpenDirection[] VALUES = values();
    private static final OpenDirection[] BY_3D_DATA = Arrays.stream(VALUES)
            .sorted(Comparator.comparingInt(direction -> direction.data3d))
            .toArray(OpenDirection[]::new);
    private static final OpenDirection[] BY_2D_DATA = Arrays.stream(VALUES)
            .filter(direction -> direction.axis().isHorizontal())
            .sorted(Comparator.comparingInt(direction -> direction.data2d))
            .toArray(OpenDirection[]::new);

    private static final ConcurrentHashMap<Integer, Collection<OpenDirection>> SET_TO_VALUES = new ConcurrentHashMap<>();


    OpenDirection(int data3d, int oppositeIndex, int data2d, String name, AxisDirection axisDirection, Axis axis, OpenVector3i normal) {
        this.data3d = data3d;
        this.data2d = data2d;
        this.oppositeIndex = oppositeIndex;
        this.name = name;
        this.axis = axis;
        this.axisDirection = axisDirection;
        this.normal = normal;
    }

    public static Collection<OpenDirection> valuesFromSet(int set) {
        // 0x3f => 0011 1111
        return SET_TO_VALUES.computeIfAbsent(set & 0x3f, it -> {
            var dirs = new ArrayList<OpenDirection>();
            for (var dir : OpenDirection.values()) {
                if ((it & (1 << dir.get3DDataValue())) != 0) {
                    dirs.add(dir);
                }
            }
            return dirs;
        });
    }

    public static Stream<OpenDirection> stream() {
        return Stream.of(VALUES);
    }

    @Override
    public int get3DDataValue() {
        return this.data3d;
    }

    public int get2DDataValue() {
        return this.data2d;
    }

    public AxisDirection axisDirection() {
        return this.axisDirection;
    }


    public OpenDirection opposite() {
        return from3DDataValue(this.oppositeIndex);
    }

    public OpenDirection clockWise() {
        return switch (this) {
            case NORTH -> EAST;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            case EAST -> SOUTH;
            default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        };
    }

    public OpenDirection counterClockWise() {
        return switch (this) {
            case NORTH -> WEST;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
            case EAST -> NORTH;
            default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
        };
    }

    public int stepX() {
        return this.normal.x();
    }

    public int stepY() {
        return this.normal.y();
    }

    public int stepZ() {
        return this.normal.z();
    }

    public String serializedName() {
        return this.name;
    }

    public Axis axis() {
        return this.axis;
    }

    public OpenVector3i normal() {
        return this.normal;
    }

    public static OpenDirection from3DDataValue(int i) {
        return BY_3D_DATA[Math.abs(i % BY_3D_DATA.length)];
    }

    public static OpenDirection from2DDataValue(int i) {
        return BY_2D_DATA[Math.abs(i % BY_2D_DATA.length)];
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static OpenDirection get(AxisDirection axisDirection, Axis axis) {
        for (OpenDirection direction : VALUES) {
            if (direction.axisDirection() == axisDirection && direction.axis() == axis) {
                return direction;
            }
        }

        throw new IllegalArgumentException("No such direction: " + axisDirection + " " + axis);
    }


    public enum Axis implements Predicate<OpenDirection> {
        X("x") {
            @Override
            public int choose(int x, int y, int z) {
                return x;
            }

            @Override
            public double choose(double x, double y, double z) {
                return x;
            }
        },
        Y("y") {
            @Override
            public int choose(int x, int y, int z) {
                return y;
            }

            @Override
            public double choose(double x, double y, double z) {
                return y;
            }
        },
        Z("z") {
            @Override
            public int choose(int x, int y, int z) {
                return z;
            }

            @Override
            public double choose(double x, double y, double z) {
                return z;
            }
        };

        private final String name;

        Axis(String name) {
            this.name = name;
        }

        public boolean isVertical() {
            return this == Y;
        }

        public boolean isHorizontal() {
            return this == X || this == Z;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public boolean test(@Nullable OpenDirection direction) {
            return direction != null && direction.axis() == this;
        }

        public Plane plane() {
            return switch (this) {
                case X, Z -> Plane.HORIZONTAL;
                case Y -> Plane.VERTICAL;
            };
        }

        public abstract int choose(int x, int y, int z);

        public abstract double choose(double x, double y, double z);
    }

    public enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int step;
        private final String name;

        AxisDirection(final int j, final String string2) {
            this.step = j;
            this.name = string2;
        }

        public int step() {
            return this.step;
        }

        public AxisDirection opposite() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum Plane implements Predicate<OpenDirection> {
        HORIZONTAL(new OpenDirection[]{NORTH, EAST, SOUTH, WEST}, new Axis[]{Axis.X, Axis.Z}),
        VERTICAL(new OpenDirection[]{UP, DOWN}, new Axis[]{Axis.Y});

        private final OpenDirection[] faces;
        private final Axis[] axis;

        Plane(final OpenDirection[] directions, final Axis[] axiss) {
            this.faces = directions;
            this.axis = axiss;
        }

        @Override
        public boolean test(@Nullable OpenDirection direction) {
            return direction != null && direction.axis().plane() == this;
        }

        public int length() {
            return this.faces.length;
        }
    }
}
