package moe.plushie.armourers_workshop.core.skin.type;

import net.minecraft.util.math.AxisAlignedBB;

public class Rectangle3D {

    public final static Rectangle3D ZERO = new Rectangle3D(0, 0, 0, 0, 0, 0);

    private int x;
    private int y;
    private int z;
    private int width;
    private int height;
    private int depth;

    public Rectangle3D(int x, int y, int z, int width, int height, int depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Rectangle3D(AxisAlignedBB box) {
        this((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.getXsize(), (int) box.getYsize(), (int) box.getZsize());
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getMinX() {
        return this.x;
    }

    public int getMinY() {
        return this.y;
    }

    public int getMinZ() {
        return this.z;
    }

    public int getMidX() {
        return this.x + this.width / 2;
    }

    public int getMidY() {
        return this.y + this.height / 2;
    }

    public int getMidZ() {
        return this.z + this.depth / 2;
    }

    public int getMaxX() {
        return this.x + this.width;
    }

    public int getMaxY() {
        return this.y + this.height;
    }

    public int getMaxZ() {
        return this.z + this.depth;
    }


    public Rectangle3D offset(Point3D point) {
        return new Rectangle3D(x + point.getX(), y + point.getY(), z + point.getZ(), width, height, depth);
    }


    @Override
    public String toString() {
        return "Rectangle3D [x=" + x + ", y=" + y + ", z=" + z + ", width="
                + width + ", height=" + height + ", depth=" + depth + "]";
    }
}
