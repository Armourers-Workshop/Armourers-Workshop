package moe.plushie.armourers_workshop.api.math;

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
}
