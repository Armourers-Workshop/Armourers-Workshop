package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Rectangle3f {

    public final static Rectangle3f ZERO = new Rectangle3f(0, 0, 0, 0, 0, 0);

    private float x;
    private float y;
    private float z;
    private float width;
    private float height;
    private float depth;

    public Rectangle3f(Rectangle3i rectangle) {
        this.x = rectangle.getX();
        this.y = rectangle.getY();
        this.z = rectangle.getZ();
        this.width = rectangle.getWidth();
        this.height = rectangle.getHeight();
        this.depth = rectangle.getDepth();
    }

    public Rectangle3f(float x, float y, float z, float width, float height, float depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Rectangle3f(AxisAlignedBB box) {
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

    public Rectangle3f getBounds() {
        return new Rectangle3f(-width / 2, -height / 2, -depth / 2, width, height, depth);
    }

    public Rectangle3f copy() {
        return new Rectangle3f(x, y, z, width, height, depth);
    }


    public Rectangle3f scale(float s) {
        return new Rectangle3f(x * s, y * s, z * s, width * s, height * s, depth * s);
    }

    public Rectangle3f offset(Vector3f point) {
        return offset(point.x(), point.y(), point.z());
    }

    public Rectangle3f offset(float dx, float dy, float dz) {
        return new Rectangle3f(x + dx, y + dy, z + dz, width, height, depth);
    }

    public void mul(Quaternion quaternion) {
        mul(new Matrix4f(quaternion));
    }

    public void mul(Matrix4f matrix) {
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

    public AxisAlignedBB asAxisAlignedBB() {
        return new AxisAlignedBB(x, y, z, x + width, y + height, z + depth);
    }

    @Override
    public String toString() {
        return "Rectangle3f [x=" + x + ", y=" + y + ", z=" + z + ", width="
                + width + ", height=" + height + ", depth=" + depth + "]";
    }
}
