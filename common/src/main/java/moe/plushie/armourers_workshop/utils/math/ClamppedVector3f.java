package moe.plushie.armourers_workshop.utils.math;

public class ClamppedVector3f extends Vector3f {

    private final float minX;
    private final float minY;
    private final float minZ;
    private final float maxX;
    private final float maxY;
    private final float maxZ;

    public ClamppedVector3f(float x, float y, float z) {
        this(x, y, z, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public ClamppedVector3f(float x, float y, float z, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        super(x, y, z);
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    @Override
    public void set(float x, float y, float z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    @Override
    public void set(Vector3f pos) {
        setX(pos.getX());
        setY(pos.getY());
        setZ(pos.getZ());
    }

    @Override
    public void set(float[] values) {
        setX(values[0]);
        setY(values[1]);
        setZ(values[2]);
    }

    @Override
    public void setX(float x) {
        super.setX(clamp(x, minX, maxX));
    }

    @Override
    public void setY(float y) {
        super.setY(clamp(y, minY, maxY));
    }

    @Override
    public void setZ(float z) {
        super.setZ(clamp(z, minZ, maxZ));
    }

    private float clamp(float value, float minValue, float maxValue) {
        if (value < minValue) {
            return minValue;
        }
        if (value > maxValue) {
            return maxValue;
        }
        return value;
    }
}
