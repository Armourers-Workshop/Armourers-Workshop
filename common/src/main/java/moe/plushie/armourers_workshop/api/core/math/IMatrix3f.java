package moe.plushie.armourers_workshop.api.core.math;

import java.nio.FloatBuffer;

public interface IMatrix3f {

    void set(IMatrix3f matrix);

    void load(FloatBuffer buffer);

    void store(FloatBuffer buffer);

    void scale(float x, float y, float z);

    void rotate(IQuaternionf quaternion);

    void multiply(IMatrix3f matrix);

    void multiply(float[] values);

    void invert();

    void transpose();

    default void scale(double x, double y, double z) {
        scale((float) x, (float) y, (float) z);
    }

    default void scale(IVector3f vector) {
        scale(vector.x(), vector.y(), vector.z());
    }

    default void scale(IVector3d vector) {
        scale(vector.x(), vector.y(), vector.z());
    }
}
