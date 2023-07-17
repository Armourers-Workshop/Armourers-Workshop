package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import net.minecraft.core.Position;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class Rectangle3f implements IRectangle3f {

    public final static Rectangle3f ZERO = new Rectangle3f(0, 0, 0, 0, 0, 0);

    private float x;
    private float y;
    private float z;
    private float width;
    private float height;
    private float depth;

    public Rectangle3f(Rectangle3i rect) {
        this.x = rect.getX();
        this.y = rect.getY();
        this.z = rect.getZ();
        this.width = rect.getWidth();
        this.height = rect.getHeight();
        this.depth = rect.getDepth();
    }

    public Rectangle3f(float x, float y, float z, float width, float height, float depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Rectangle3f(AABB box) {
        this((float) box.minX, (float) box.minY, (float) box.minZ, (float) box.getXsize(), (float) box.getYsize(), (float) box.getZsize());
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return this.z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getDepth() {
        return this.depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public float getMinX() {
        return this.x;
    }

    public float getMinY() {
        return this.y;
    }

    public float getMinZ() {
        return this.z;
    }

    public float getMidX() {
        return this.x + this.width / 2;
    }

    public float getMidY() {
        return this.y + this.height / 2;
    }

    public float getMidZ() {
        return this.z + this.depth / 2;
    }

    public float getMaxX() {
        return this.x + this.width;
    }

    public float getMaxY() {
        return this.y + this.height;
    }

    public float getMaxZ() {
        return this.z + this.depth;
    }

    public Vector3f getCenter() {
        return new Vector3f(getMidX(), getMidY(), getMidZ());
    }

    public Vector3f getOrigin() {
        return new Vector3f(x, y, z);
    }

    public Rectangle3f getBounds() {
        return new Rectangle3f(-width / 2, -height / 2, -depth / 2, width, height, depth);
    }

    public Rectangle3f copy() {
        return new Rectangle3f(x, y, z, width, height, depth);
    }

    public Rectangle3f scale(float s) {
        return new Rectangle3f(x * s, y * s, z * s, width * s, height * s, depth * s);
    }

    public Rectangle3f offset(Position point) {
        return offset((float) point.x(), (float) point.y(), (float) point.z());
    }

    public Rectangle3f offset(float dx, float dy, float dz) {
        return new Rectangle3f(x + dx, y + dy, z + dz, width, height, depth);
    }

    public Rectangle3f inflate(float value) {
        if (value == 0) {
            return this;
        }
        float v2 = value + value;
        return new Rectangle3f(x - value, y - value, z - value, width + v2, height + v2, depth + v2);
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

    public void mul(OpenQuaternionf quaternion) {
        mul(new OpenMatrix4f(quaternion));
    }

    public void mul(OpenMatrix4f matrix) {
        List<Vector4f> vertexes = Arrays.asList(
                new Vector4f(x, y, z, 1.0f),
                new Vector4f(x + width, y, z, 1.0f),
                new Vector4f(x + width, y + height, z, 1.0f),
                new Vector4f(x + width, y + height, z + depth, 1.0f),
                new Vector4f(x + width, y, z + depth, 1.0f),
                new Vector4f(x, y + height, z, 1.0f),
                new Vector4f(x, y + height, z + depth, 1.0f),
                new Vector4f(x, y, z + depth, 1.0f)
        );
        Iterator<Vector4f> iterator = vertexes.iterator();
        Vector4f point = iterator.next();
        point.transform(matrix);
        float minX = point.x(), minY = point.y(), minZ = point.z();
        float maxX = point.x(), maxY = point.y(), maxZ = point.z();
        while (iterator.hasNext()) {
            point = iterator.next();
            point.transform(matrix);
            minX = Math.min(minX, point.x());
            minY = Math.min(minY, point.y());
            minZ = Math.min(minZ, point.z());
            maxX = Math.max(maxX, point.x());
            maxY = Math.max(maxY, point.y());
            maxZ = Math.max(maxZ, point.z());
        }
        x = minX;
        y = minY;
        z = minZ;
        width = maxX - minX;
        height = maxY - minY;
        depth = maxZ - minZ;
    }

    public AABB asAABB() {
        return new AABB(x, y, z, x + width, y + height, z + depth);
    }

    @Override
    public String toString() {
        return String.format("(%g %g %g; %g %g %g)", x, y, z, width, height, depth);
    }
}
