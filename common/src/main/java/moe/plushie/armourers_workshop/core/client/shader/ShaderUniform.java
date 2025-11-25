package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.utils.MatrixUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Supplier;

public abstract class ShaderUniform {

    protected final String name;

    protected int program = -1;
    protected int location = -1;

    ShaderUniform(String name) {
        this.name = name;
    }

    public static ShaderUniform integer(String name, Supplier<Integer> value) {
        return new IntValue(name, value);
    }

    public static ShaderUniform vector4f(String name, Supplier<OpenVector4f> value) {
        return new Vec4fValue(name, value);
    }

    public static ShaderUniform matrix4f(String name, Supplier<OpenMatrix4f> value) {
        return new Matrix4fValue(name, value);
    }

    public static ShaderUniform matrix3f(String name, Supplier<OpenMatrix3f> value) {
        return new Matrix3fValue(name, value);
    }


    public abstract void apply();

    public void push() {
    }

    public void pop() {
    }

    public void link(int program) {
        this.program = program;
        this.location = GL20.glGetUniformLocation(program, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShaderUniform that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean isLinked() {
        return location != -1;
    }

    private static class IntValue extends ShaderUniform {

        private final Supplier<Integer> value;
        private final Stack<Integer> cachedValues = new Stack<>();
        private int cachedValue = 0;

        IntValue(String name, Supplier<Integer> value) {
            super(name);
            this.value = value;
        }

        @Override
        public void apply() {
            int newValue = value.get();
            if (cachedValue != newValue) {
                cachedValue = newValue;
                GL20.glUniform1i(location, newValue);
            }
        }

        @Override
        public void push() {
            cachedValues.push(cachedValue);
        }

        @Override
        public void pop() {
            int newValue = cachedValues.pop();
            cachedValue = newValue;
            GL20.glUniform1i(location, newValue);
        }
    }

    private static class Vec4fValue extends ShaderUniform {

        private final Supplier<OpenVector4f> value;
        private OpenVector4f cachedValue = OpenVector4f.ZERO;

        Vec4fValue(String name, Supplier<OpenVector4f> value) {
            super(name);
            this.value = value;
        }

        @Override
        public void apply() {
            var newValue = value.get();
            if (!newValue.equals(cachedValue)) {
                cachedValue = newValue;
                GL20.glUniform4f(location, newValue.x(), newValue.y(), newValue.z(), newValue.w());
            }
        }
    }

    private static class Matrix4fValue extends ShaderUniform {

        private final FloatBuffer buffer = MatrixUtils.createFloatBuffer(16);
        private final Supplier<OpenMatrix4f> value;
        private final OpenMatrix4f cachedValue = OpenMatrix4f.createScaleMatrix(0, 0, 0);

        Matrix4fValue(String name, Supplier<OpenMatrix4f> value) {
            super(name);
            this.value = value;
        }

        @Override
        public void apply() {
            var newValue = value.get();
            if (!newValue.equals(cachedValue)) {
                newValue.store(buffer);
                cachedValue.load(buffer);
                buffer.rewind();
                GL20.glUniformMatrix4fv(location, false, buffer);
            }
        }
    }

    private static class Matrix3fValue extends ShaderUniform {

        private final FloatBuffer buffer = MatrixUtils.createFloatBuffer(9);
        private final Supplier<OpenMatrix3f> value;
        private final OpenMatrix3f cachedValue = OpenMatrix3f.createScaleMatrix(0, 0, 0);

        Matrix3fValue(String name, Supplier<OpenMatrix3f> value) {
            super(name);
            this.value = value;
        }

        @Override
        public void apply() {
            var newValue = value.get();
            if (!newValue.equals(cachedValue)) {
                newValue.store(buffer);
                cachedValue.load(buffer);
                buffer.rewind();
                GL20.glUniformMatrix3fv(location, false, buffer);
            }
        }
    }
}
