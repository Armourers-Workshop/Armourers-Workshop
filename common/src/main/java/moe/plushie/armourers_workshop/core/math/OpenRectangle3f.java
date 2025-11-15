package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3i;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.List;

@SuppressWarnings("unused")
public class OpenRectangle3f implements IRectangle3f {

    public static final int BYTES = Float.BYTES * 6;

    public final static OpenRectangle3f ZERO = new OpenRectangle3f(0, 0, 0, 0, 0, 0);

    public final static OpenRectangle3f ONE = new OpenRectangle3f(0, 0, 0, 1, 1, 1);

    public static final IDataCodec<OpenRectangle3f> CODEC = IDataCodec.FLOAT.listOf().xmap(OpenRectangle3f::new, OpenRectangle3f::toList);

    public float x;
    public float y;
    public float z;
    public float width;
    public float height;
    public float depth;

    public OpenRectangle3f(IRectangle3i rect) {
        this(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth());
    }

    public OpenRectangle3f(IRectangle3f rect) {
        this(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth());
    }

    public OpenRectangle3f(float x, float y, float z, float width, float height, float depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public OpenRectangle3f(double x, double y, double z, double width, double height, double depth) {
        this((float) x, (float) y, (float) z, (float) width, (float) height, (float) depth);
    }

    public OpenRectangle3f(List<Float> list) {
        this(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5));
    }

    public void union(IRectangle3f rect) {
        float x1 = Math.min(minX(), rect.minX());
        float y1 = Math.min(minY(), rect.minY());
        float z1 = Math.min(minZ(), rect.minZ());
        float x2 = Math.max(maxX(), rect.maxX());
        float y2 = Math.max(maxY(), rect.maxY());
        float z2 = Math.max(maxZ(), rect.maxZ());
        x = x1;
        y = y1;
        z = z1;
        width = x2 - x1;
        height = y2 - y1;
        depth = z2 - z1;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    @Override
    public float x() {
        return this.x;
    }

    @Override
    public float y() {
        return this.y;
    }

    @Override
    public float z() {
        return this.z;
    }

    @Override
    public float width() {
        return this.width;
    }

    @Override
    public float height() {
        return this.height;
    }

    @Override
    public float depth() {
        return this.depth;
    }

    @Override
    public float minX() {
        return this.x;
    }

    @Override
    public float minY() {
        return this.y;
    }

    @Override
    public float minZ() {
        return this.z;
    }

    @Override
    public float midX() {
        return this.x + this.width / 2;
    }

    @Override
    public float midY() {
        return this.y + this.height / 2;
    }

    @Override
    public float midZ() {
        return this.z + depth / 2;
    }

    @Override
    public float maxX() {
        return this.x + this.width;
    }

    @Override
    public float maxY() {
        return this.y + this.height;
    }

    @Override
    public float maxZ() {
        return this.z + this.depth;
    }

    public OpenVector3f center() {
        return new OpenVector3f(midX(), midY(), midZ());
    }

    public OpenVector3f origin() {
        return new OpenVector3f(x, y, z);
    }

    public OpenRectangle3f bounds() {
        return new OpenRectangle3f(-width / 2, -height / 2, -depth / 2, width, height, depth);
    }

    public OpenRectangle3f copy() {
        return new OpenRectangle3f(x, y, z, width, height, depth);
    }

    public OpenRectangle3f scale(float s) {
        return new OpenRectangle3f(x * s, y * s, z * s, width * s, height * s, depth * s);
    }

    public OpenRectangle3f offset(IVector3f point) {
        return offset(point.x(), point.y(), point.z());
    }

    public OpenRectangle3f offset(float dx, float dy, float dz) {
        return new OpenRectangle3f(x + dx, y + dy, z + dz, width, height, depth);
    }

    public OpenRectangle3f inflate(float value) {
        if (value == 0) {
            return this;
        }
        float v2 = value + value;
        return new OpenRectangle3f(x - value, y - value, z - value, width + v2, height + v2, depth + v2);
    }


//    public boolean intersects(AABB aABB) {
//        return this.intersects(aABB.minX, aABB.minY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.maxZ);
//    }
//
//    public boolean intersects(double d, double e, double f, double g, double h, double i) {
//        return this.minX < g && this.maxX > d && this.minY < h && this.maxY > e && this.minZ < i && this.maxZ > f;
//    }
//
//    public boolean intersects(Vec3 vec3, Vec3 vec32) {
//        return this.intersects(Math.min(vec3.x, vec32.x), Math.min(vec3.y, vec32.y), Math.min(vec3.z, vec32.z), Math.max(vec3.x, vec32.x), Math.max(vec3.y, vec32.y), Math.max(vec3.z, vec32.z));
//    }

    public void transform(OpenQuaternionf quaternion) {
        transform(new OpenMatrix4f(quaternion));
    }

    public void transform(OpenMatrix4f matrix) {
        var start = new OpenVector4f(x, y, z, 1.0f);
        var end = new OpenVector4f(x + width, y + height, z + depth, 1.0f);
        start.transform(matrix);
        end.transform(matrix);
        x = Math.min(start.x(), end.x());
        y = Math.min(start.y(), end.y());
        z = Math.min(start.z(), end.z());
        width = Math.max(start.x(), end.x()) - x;
        height = Math.max(start.y(), end.y()) - y;
        depth = Math.max(start.z(), end.z()) - z;
    }

    public List<Float> toList() {
        return Collections.newList(x, y, z, width, height, depth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenRectangle3f that)) return false;
        return Float.compare(x, that.x) == 0 && Float.compare(y, that.y) == 0 && Float.compare(z, that.z) == 0 && Float.compare(width, that.width) == 0 && Float.compare(height, that.height) == 0 && Float.compare(depth, that.depth) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, width, height, depth);
    }

    @Override
    public String toString() {
        return OpenMath.format("(%f %f %f; %f %f %f)", x, y, z, width, height, depth);
    }
}
