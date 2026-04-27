package moe.plushie.armourers_workshop.api.core.math;

import java.nio.FloatBuffer;

public interface IMatrix4f {

    void set(IMatrix4f matrix);

    void load(FloatBuffer buffer);

    void store(FloatBuffer buffer);

    void scale(float x, float y, float z);

    void translate(float x, float y, float z);

    void rotate(IQuaternionf quaternion);

    void multiply(IMatrix4f matrix);

    void multiply(float[] values);

    void invert();

    void transpose();

    default void translate(double x, double y, double z) {
        translate((float) x, (float) y, (float) z);
    }

    default void scale(double x, double y, double z) {
        scale((float) x, (float) y, (float) z);
    }

    default void translate(IVector3f translation) {
        translate(translation.x(), translation.y(), translation.z());
    }

    default void scale(IVector3f vector) {
        scale(vector.x(), vector.y(), vector.z());
    }

    default void translate(IVector3d translation) {
        translate(translation.x(), translation.y(), translation.z());
    }

    default void scale(IVector3d vector) {
        scale(vector.x(), vector.y(), vector.z());
    }
}
