package moe.plushie.armourers_workshop.api.math;

import java.nio.FloatBuffer;

public interface IMatrix3f {

    void set(FloatBuffer buffer);

    void get(FloatBuffer buffer);

    void rotate(IQuaternionf quaternion);

    void multiply(IMatrix3f matrix);

    void multiply(float[] values);

    void invert();

    IMatrix3f copy();
}
