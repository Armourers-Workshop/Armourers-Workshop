package moe.plushie.armourers_workshop.utils.math;


import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IRectangle3f;

import java.util.ArrayList;

/**
 * Axis aligned bounding box
 */
public class OpenBoundingBox {

    public static final OpenBoundingBox ZERO = new OpenBoundingBox(Vector3f.ZERO, Vector3f.ZERO);

    private float minX;
    private float minY;
    private float minZ;
    private float maxX;
    private float maxY;
    private float maxZ;

    public OpenBoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public OpenBoundingBox(Vector3f min, Vector3f max) {
        this(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    public OpenBoundingBox(IRectangle3f rect) {
        this(rect.getMinX(), rect.getMinY(), rect.getMinZ(), rect.getMaxX(), rect.getMaxY(), rect.getMaxZ());
    }

    public void transform(IMatrix4f matrix) {
        ArrayList<Vector3f> vertices = new ArrayList<>();
        vertices.add(new Vector3f(minX, minY, minZ));
        vertices.add(new Vector3f(maxX, minY, minZ));
        vertices.add(new Vector3f(minX, maxY, minZ));
        vertices.add(new Vector3f(maxX, maxY, minZ));
        vertices.add(new Vector3f(minX, minY, maxZ));
        vertices.add(new Vector3f(maxX, minY, maxZ));
        vertices.add(new Vector3f(minX, maxY, maxZ));
        vertices.add(new Vector3f(maxX, maxY, maxZ));
        minX = Float.MAX_VALUE;
        minY = Float.MAX_VALUE;
        minZ = Float.MAX_VALUE;
        maxX = Float.MIN_VALUE;
        maxY = Float.MIN_VALUE;
        maxZ = Float.MIN_VALUE;
        for (Vector3f vertex : vertices) {
            vertex.transform(matrix);
            minX = Math.min(minX, vertex.getX());
            minY = Math.min(minY, vertex.getY());
            minZ = Math.min(minZ, vertex.getZ());
            maxX = Math.max(maxX, vertex.getX());
            maxY = Math.max(maxY, vertex.getY());
            maxZ = Math.max(maxZ, vertex.getZ());
        }
    }

    public boolean intersects(OpenBoundingBox box) {
        return intersects(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public boolean intersects(float d, float e, float f, float g, float h, float i) {
        return minX < g && maxX > d && minY < h && maxY > e && minZ < i && maxZ > f;
    }

    public boolean intersects(OpenRay ray) {
        return ray.intersects(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public OpenBoundingBox transforming(IMatrix4f matrix) {
        OpenBoundingBox ret = copy();
        ret.transform(matrix);
        return ret;
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMinZ() {
        return minZ;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getMaxZ() {
        return maxZ;
    }

    public OpenBoundingBox copy() {
        return new OpenBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
