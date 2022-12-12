package moe.plushie.armourers_workshop.api.math;

import java.nio.FloatBuffer;

public interface IMatrix4f {

    void set(FloatBuffer buffer);

    void get(FloatBuffer buffer);

    void scale(float x, float y, float z);

    void translate(float x, float y, float z);

    void rotate(IQuaternionf quaternion);

    void multiply(IMatrix4f matrix);

    void multiply(float[] values);

    void invert();

    IMatrix4f copy();
}
