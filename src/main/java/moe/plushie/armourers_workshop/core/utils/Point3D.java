package moe.plushie.armourers_workshop.core.utils;

public class Point3D {

    public final static Point3D ZERO = new Point3D(0, 0, 0);

    private final int x;
    private final int y;
    private final int z;
    
    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public String toString() {
        return "Point3D [x=" + x + ", y=" + y + ", z=" + z + "]";
    }
}
