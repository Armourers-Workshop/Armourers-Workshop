package moe.plushie.armourers_workshop.core.skin.cube;

public class SkinCubePartition {

//
//    public Axis x() {
//        return x;
//    }
//
//    public Axis y() {
//        return y;
//    }
//
//    public Axis z() {
//        return z;
//    }
//

    public static Builder create(int x, int y, int z, int width, int height, int depth) {
        return new Builder();
    }

    public static class Builder {


        public Builder x(int joint) {
            return this;
        }

        public Builder y(int joint) {
            return this;
        }

        public Builder z(int joint) {
            return this;
        }

        public SkinCubePartition build() {
            return null;
        }
    }

    public static void qq() {
        //fr = new Rectangle3i(-4, -8, -4, 8, 8, 8);

        SkinCubePartition.create(-4, -8, -4, 8, 8, 8).y(2).y(3).build();
    }
}
