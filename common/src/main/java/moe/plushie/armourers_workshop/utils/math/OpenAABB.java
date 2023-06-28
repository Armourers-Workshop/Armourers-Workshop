package moe.plushie.armourers_workshop.utils.math;


import moe.plushie.armourers_workshop.api.math.IMatrix4f;

public class OpenAABB {

    public static final OpenAABB ZERO = new OpenAABB(Vector3f.ZERO, Vector3f.ZERO);

    private Vector3f min;
    private Vector3f max;

    public OpenAABB(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }

    public OpenAABB(Rectangle3f rect) {
        this.min = new Vector3f(rect.getMinX(), rect.getMinY(), rect.getMinZ());
        this.max = new Vector3f(rect.getMaxX(), rect.getMaxY(), rect.getMaxZ());
    }

    public void transform(IMatrix4f matrix) {
        this.min = min.transforming(matrix);
        this.max = max.transforming(matrix);
    }

    public boolean intersects(OpenRay ray) {
        return ray.intersects(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    public OpenAABB transforming(IMatrix4f matrix) {
        OpenAABB ret = copy();
        ret.transform(matrix);
        return ret;
    }

    public OpenAABB copy() {
        return new OpenAABB(min, max);
    }
}
