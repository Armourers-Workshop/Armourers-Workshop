package moe.plushie.armourers_workshop.core.utils;

public class OpenOrientation {

    private final OpenDirection up;
    private final OpenDirection front;
    private final SideBias sideBias;

    public OpenOrientation(final OpenDirection up, final OpenDirection front, final SideBias sideBias) {
        this.up = up;
        this.front = front;
        this.sideBias = sideBias;
//        Vec3i rightVector = front.getUnitVec3i().cross(up.getUnitVec3i());
//        Direction side = Direction.getNearest(rightVector, null);
//        Direction side = Direction.getNearest(rightVector, null);
//        Objects.requireNonNull(side);
//        if (this.sideBias == Orientation.SideBias.RIGHT) {
//            this.side = side;
//        } else {
//            this.side = side.getOpposite();
//        }
    }

    public OpenDirection front() {
        return this.front;
    }

    public OpenDirection up() {
        return this.up;
    }

    //public OpenDirection side() {
    //    return this.side;
    //}

    public enum SideBias {
        LEFT("left"),
        RIGHT("right");

        private final String name;

        SideBias(final String name) {
            this.name = name;
        }

        public SideBias getOpposite() {
            return this == LEFT ? RIGHT : LEFT;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
