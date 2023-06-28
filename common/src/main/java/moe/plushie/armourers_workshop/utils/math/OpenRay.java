package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.IMatrix4f;

public class OpenRay {

    public Vector3f origin;
    public Vector3f direction;

    public OpenRay(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public boolean intersects(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        // https://tavianator.com/2011/ray_box.html
        // https://tavianator.com/2015/ray_box_nan.html
        float ix = 1.0f / direction.getX();
        float iy = 1.0f / direction.getY();
        float iz = 1.0f / direction.getZ();

        float t1 = (minX - origin.getX()) * ix;
        float t2 = (maxX - origin.getX()) * ix;
        float t3 = (minY - origin.getY()) * iy;
        float t4 = (maxY - origin.getY()) * iy;
        float t5 = (minZ - origin.getZ()) * iz;
        float t6 = (maxZ - origin.getZ()) * iz;

        float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        // if tmax < 0, ray (line) is intersecting AABB, but the whole AABB is behind us.
        // if tmin > tmax, ray doesn't intersect AABB.
        return tmax >= 0 && tmax >= tmin;
    }

    public void transform(IMatrix4f matrix) {
        float[] v1 = {origin.getX(), origin.getY(), origin.getZ(), 1f};
        float[] v2 = {direction.getX(), direction.getY(), direction.getZ(), 0f};
        matrix.multiply(v1);
        matrix.multiply(v2);
        this.origin = new Vector3f(v1);
        this.direction = new Vector3f(v2);
    }

    public OpenRay transforming(IMatrix4f matrix) {
        OpenRay ret = copy();
        ret.transform(matrix);
        return ret;
    }

    public OpenRay copy() {
        return new OpenRay(origin, direction);
    }
}
