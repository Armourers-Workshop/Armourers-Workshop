package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3i;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class OpenRectangle3i implements IRectangle3i {

    public final static OpenRectangle3i ZERO = new OpenRectangle3i(0, 0, 0, 0, 0, 0);

    public static final IDataCodec<OpenRectangle3i> CODEC = IDataCodec.INT.listOf().xmap(OpenRectangle3i::new, OpenRectangle3i::toList);

    public int x;
    public int y;
    public int z;
    public int width;
    public int height;
    public int depth;

    public OpenRectangle3i() {
    }

    public OpenRectangle3i(int x, int y, int z, int width, int height, int depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public OpenRectangle3i(IRectangle3i rect) {
        this(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth());
    }

    public OpenRectangle3i(IRectangle3f rect) {
        this(OpenMath.floori(rect.x()), OpenMath.floori(rect.y()), OpenMath.floori(rect.z()), OpenMath.floori(rect.width()), OpenMath.floori(rect.height()), OpenMath.floori(rect.depth()));
    }

//    public Rectangle3i(AABB box) {
//        this((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.getXsize(), (int) box.getYsize(), (int) box.getZsize());
//    }

    public OpenRectangle3i(List<Integer> list) {
        this(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5));
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public int x() {
        return this.x;
    }

    @Override
    public int y() {
        return this.y;
    }

    @Override
    public int z() {
        return this.z;
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    @Override
    public int depth() {
        return this.depth;
    }

    @Override
    public int minX() {
        return this.x;
    }

    @Override
    public int minY() {
        return this.y;
    }

    @Override
    public int minZ() {
        return this.z;
    }

    @Override
    public int midX() {
        return this.x + this.width / 2;
    }

    @Override
    public int midY() {
        return this.y + this.height / 2;
    }

    @Override
    public int midZ() {
        return this.z + this.depth / 2;
    }

    @Override
    public int maxX() {
        return this.x + this.width;
    }

    @Override
    public int maxY() {
        return this.y + this.height;
    }

    @Override
    public int maxZ() {
        return this.z + this.depth;
    }

    public OpenVector3i origin() {
        return new OpenVector3i(x, y, z);
    }

//    public AABB asAABB() {
//        return new AABB(x, y, z, x + width, y + height, z + depth);
//    }

    public void intersection(OpenRectangle3i rect) {
        int x1 = Math.max(minX(), rect.minX());
        int y1 = Math.max(minY(), rect.minY());
        int z1 = Math.max(minZ(), rect.minZ());
        int x2 = Math.min(maxX(), rect.maxX());
        int y2 = Math.min(maxY(), rect.maxY());
        int z2 = Math.min(maxZ(), rect.maxZ());
        x = x1;
        y = y1;
        z = z1;
        width = x2 - x1;
        height = y2 - y1;
        depth = z2 - z1;
    }

    public void union(OpenRectangle3i rect) {
        int x1 = Math.min(minX(), rect.minX());
        int y1 = Math.min(minY(), rect.minY());
        int z1 = Math.min(minZ(), rect.minZ());
        int x2 = Math.max(maxX(), rect.maxX());
        int y2 = Math.max(maxY(), rect.maxY());
        int z2 = Math.max(maxZ(), rect.maxZ());
        x = x1;
        y = y1;
        z = z1;
        width = x2 - x1;
        height = y2 - y1;
        depth = z2 - z1;
    }

    public boolean contains(OpenVector3i point) {
        int x = point.x();
        int y = point.y();
        int z = point.z();
        return minX() <= x && x <= maxX()
                && minY() <= y && y <= maxY()
                && minZ() <= z && z <= maxZ();
    }

    public OpenRectangle3i offset(OpenVector3i point) {
        return offset(point.x(), point.y(), point.z());
    }

    public OpenRectangle3i offset(int tx, int ty, int tz) {
        return new OpenRectangle3i(x + tx, y + ty, z + tz, width, height, depth);
    }

    public Iterable<OpenVector3i> enumerateZYX() {
        // enumerate order is z/y/x
        return () -> new Iterator<OpenVector3i>() {

            int ix = 0;
            int iy = 0;
            int iz = 0;

            @Override
            public boolean hasNext() {
                return ix < width && iy < height && iz < depth;
            }

            @Override
            public OpenVector3i next() {
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
                return new OpenVector3i(dx, dy, dz);
            }
        };
    }

    public List<Integer> toList() {
        return Collections.newList(x, y, z, width, height, depth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenRectangle3i that)) return false;
        return x == that.x && y == that.y && z == that.z && width == that.width && height == that.height && depth == that.depth;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, width, height, depth);
    }

    @Override
    public String toString() {
        return OpenMath.format("(%d %d %d; %d %d %d)", x, y, z, width, height, depth);
    }
}
