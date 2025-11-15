package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3f;

import java.util.ArrayList;

/**
 * Axis aligned bounding box
 */
public class OpenAxisAlignedBoundingBox {

    public static final OpenAxisAlignedBoundingBox ZERO = new OpenAxisAlignedBoundingBox(OpenVector3f.ZERO, OpenVector3f.ZERO);

    private float minX;
    private float minY;
    private float minZ;
    private float maxX;
    private float maxY;
    private float maxZ;

    public OpenAxisAlignedBoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public OpenAxisAlignedBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this((float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ);
    }

    public OpenAxisAlignedBoundingBox(OpenVector3f min, OpenVector3f max) {
        this(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
    }

    public OpenAxisAlignedBoundingBox(IRectangle3f rect) {
        this(rect.minX(), rect.minY(), rect.minZ(), rect.maxX(), rect.maxY(), rect.maxZ());
    }

    public void transform(IMatrix4f matrix) {
        var vertices = new ArrayList<OpenVector3f>();
        vertices.add(new OpenVector3f(minX, minY, minZ));
        vertices.add(new OpenVector3f(maxX, minY, minZ));
        vertices.add(new OpenVector3f(minX, maxY, minZ));
        vertices.add(new OpenVector3f(maxX, maxY, minZ));
        vertices.add(new OpenVector3f(minX, minY, maxZ));
        vertices.add(new OpenVector3f(maxX, minY, maxZ));
        vertices.add(new OpenVector3f(minX, maxY, maxZ));
        vertices.add(new OpenVector3f(maxX, maxY, maxZ));
        minX = Float.MAX_VALUE;
        minY = Float.MAX_VALUE;
        minZ = Float.MAX_VALUE;
        maxX = Float.MIN_VALUE;
        maxY = Float.MIN_VALUE;
        maxZ = Float.MIN_VALUE;
        for (var vertex : vertices) {
            vertex.transform(matrix);
            minX = Math.min(minX, vertex.x());
            minY = Math.min(minY, vertex.y());
            minZ = Math.min(minZ, vertex.z());
            maxX = Math.max(maxX, vertex.x());
            maxY = Math.max(maxY, vertex.y());
            maxZ = Math.max(maxZ, vertex.z());
        }
    }

    public boolean intersects(OpenAxisAlignedBoundingBox box) {
        return intersects(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public boolean intersects(float d, float e, float f, float g, float h, float i) {
        return minX < g && maxX > d && minY < h && maxY > e && minZ < i && maxZ > f;
    }

    public boolean intersects(OpenRay ray) {
        return ray.intersects(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public OpenAxisAlignedBoundingBox transforming(IMatrix4f matrix) {
        var ret = copy();
        ret.transform(matrix);
        return ret;
    }

    public float minX() {
        return minX;
    }

    public float minY() {
        return minY;
    }

    public float minZ() {
        return minZ;
    }

    public float maxX() {
        return maxX;
    }

    public float maxY() {
        return maxY;
    }

    public float maxZ() {
        return maxZ;
    }

    public float width() {
        return maxX - minX;
    }

    public float height() {
        return maxY - minY;
    }

    public float depth() {
        return maxZ - minZ;
    }

    public OpenAxisAlignedBoundingBox copy() {
        return new OpenAxisAlignedBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
