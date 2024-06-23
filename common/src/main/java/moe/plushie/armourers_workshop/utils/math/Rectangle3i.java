package moe.plushie.armourers_workshop.utils.math;

import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.math.IRectangle3i;
import moe.plushie.armourers_workshop.utils.MathUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class Rectangle3i implements IRectangle3i {

    public final static Rectangle3i ZERO = new Rectangle3i(0, 0, 0, 0, 0, 0);

    private int x;
    private int y;
    private int z;
    private int width;
    private int height;
    private int depth;

    public Rectangle3i(int x, int y, int z, int width, int height, int depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Rectangle3i(IRectangle3i rect) {
        this(rect.getX(), rect.getY(), rect.getZ(), rect.getWidth(), rect.getHeight(), rect.getDepth());
    }

    public Rectangle3i(IRectangle3f rect) {
        this(MathUtils.floor(rect.getX()), MathUtils.floor(rect.getY()), MathUtils.floor(rect.getZ()), MathUtils.floor(rect.getWidth()), MathUtils.floor(rect.getHeight()), MathUtils.floor(rect.getDepth()));
    }

//    public Rectangle3i(AABB box) {
//        this((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.getXsize(), (int) box.getYsize(), (int) box.getZsize());
//    }

    public Rectangle3i(List<Integer> list) {
        this(list[0], list[1], list[2], list[3], list[4], list[5]);
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

    public Vector3i getOrigin() {
        return new Vector3i(x, y, z);
    }

//    public AABB asAABB() {
//        return new AABB(x, y, z, x + width, y + height, z + depth);
//    }

    public void intersection(Rectangle3i rect) {
        int x1 = Math.max(getMinX(), rect.getMinX());
        int y1 = Math.max(getMinY(), rect.getMinY());
        int z1 = Math.max(getMinZ(), rect.getMinZ());
        int x2 = Math.min(getMaxX(), rect.getMaxX());
        int y2 = Math.min(getMaxY(), rect.getMaxY());
        int z2 = Math.min(getMaxZ(), rect.getMaxZ());
        x = x1;
        y = y1;
        z = z1;
        width = x2 - x1;
        height = y2 - y1;
        depth = z2 - z1;
    }

    public void union(Rectangle3i rect) {
        int x1 = Math.min(getMinX(), rect.getMinX());
        int y1 = Math.min(getMinY(), rect.getMinY());
        int z1 = Math.min(getMinZ(), rect.getMinZ());
        int x2 = Math.max(getMaxX(), rect.getMaxX());
        int y2 = Math.max(getMaxY(), rect.getMaxY());
        int z2 = Math.max(getMaxZ(), rect.getMaxZ());
        x = x1;
        y = y1;
        z = z1;
        width = x2 - x1;
        height = y2 - y1;
        depth = z2 - z1;
    }

    public boolean contains(Vector3i point) {
        int x = point.getX();
        int y = point.getY();
        int z = point.getZ();
        return getMinX() <= x && x <= getMaxX()
                && getMinY() <= y && y <= getMaxY()
                && getMinZ() <= z && z <= getMaxZ();
    }

    public Rectangle3i offset(Vector3i point) {
        return offset(point.getX(), point.getY(), point.getZ());
    }

    public Rectangle3i offset(int tx, int ty, int tz) {
        return new Rectangle3i(x + tx, y + ty, z + tz, width, height, depth);
    }

    public Iterable<Vector3i> enumerateZYX() {
        // enumerate order is z/y/x
        return () -> new Iterator<Vector3i>() {

            int ix = 0;
            int iy = 0;
            int iz = 0;

            @Override
            public boolean hasNext() {
                return ix < width && iy < height && iz < depth;
            }

            @Override
            public Vector3i next() {
                int dx = ix + x;
                int dy = iy + y;
                int dz = iz + z;
                iz += 1;
                if (iz >= depth) {
                    iz = 0;
                    iy += 1;
                }
                if (iy >= height) {
                    iy = 0;
                    ix += 1;
                }
                return new Vector3i(dx, dy, dz);
            }
        };
    }

    public List<Integer> toList() {
        return Lists.newArrayList(x, y, z, width, height, depth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rectangle3i that)) return false;
        return x == that.x && y == that.y && z == that.z && width == that.width && height == that.height && depth == that.depth;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, width, height, depth);
    }

    @Override
    public String toString() {
        return String.format("(%d %d %d; %d %d %d)", x, y, z, width, height, depth);
    }
}
