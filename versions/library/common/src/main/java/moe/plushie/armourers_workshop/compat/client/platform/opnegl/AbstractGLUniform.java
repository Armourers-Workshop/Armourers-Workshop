package moe.plushie.armourers_workshop.compat.client.platform.opnegl;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.utils.MatrixUtils;
import moe.plushie.armourers_workshop.core.utils.Objects;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.Stack;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGLUniform<T> {

    protected final String name;

    protected final Stack<T> stack = new Stack<>();
    protected final Supplier<T> value;

    protected T lastValue;

    protected int program = -1;
    protected int location = -1;

    protected AbstractGLUniform(String name, Supplier<T> value, T defaultValue) {
        this.name = name;
        this.value = value;
        this.lastValue = defaultValue;
    }

    public static AbstractGLUniform<Integer> integer(String name, Supplier<Integer> value) {
        return new IntValue(name, value);
    }

    public static AbstractGLUniform<OpenVector4f> vector4f(String name, Supplier<OpenVector4f> value) {
        return new Vec4fValue(name, value);
    }

    public static AbstractGLUniform<OpenMatrix4f> matrix4f(String name, Supplier<OpenMatrix4f> value) {
        return new Matrix4fValue(name, value);
    }

    public static AbstractGLUniform<OpenMatrix3f> matrix3f(String name, Supplier<OpenMatrix3f> value) {
        return new Matrix3fValue(name, value);
    }

    /**
     * Save the current OpenGL uniform state.
     */
    public void push() {
        stack.push(lastValue);
        lastValue = download();
    }

    /**
     * Applies the current state into OpenGL if necessary.
     */
    public void apply() {
        var newValue = value.get();
        if (!Objects.equals(newValue, lastValue)) {
            lastValue = upload(newValue);
        }
    }

    /**
     * Restore the uniform state into OpenGL.
     */
    public void pop() {
        var oldValue = lastValue;
        lastValue = stack.pop();
        if (!Objects.equals(lastValue, oldValue)) {
            upload(lastValue);
        }
    }

    public void link(int program) {
        this.program = program;
        this.location = GL20.glGetUniformLocation(program, name);
    }

    protected abstract T upload(T newValue);

    protected abstract T download();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractGLUniform<?> that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean isLinked() {
        return location != -1;
    }

    private static class IntValue extends AbstractGLUniform<Integer> {

        protected IntValue(String name, Supplier<Integer> value) {
            super(name, value, 0);
        }

        @Override
        protected Integer upload(Integer newValue) {
            GL20.glUniform1i(location, newValue);
            return newValue;
        }

        @Override
        protected Integer download() {
            return GL20.glGetUniformi(program, location);
        }
    }

    private static class Vec4fValue extends AbstractGLUniform<OpenVector4f> {

        private final OpenVector4f sharedValue = new OpenVector4f();
        private final FloatBuffer sharedBuffer = MatrixUtils.createFloatBuffer(4);

        protected Vec4fValue(String name, Supplier<OpenVector4f> value) {
            super(name, value, new OpenVector4f());
        }

        @Override
        protected OpenVector4f upload(OpenVector4f newValue) {
            sharedValue.set(newValue);
            GL20.glUniform4f(location, newValue.x(), newValue.y(), newValue.z(), newValue.w());
            return sharedValue;
        }

        @Override
        protected OpenVector4f download() {
            GL20.glGetUniformfv(program, location, sharedBuffer);
            return new OpenVector4f(sharedBuffer);
        }
    }

    private static class Matrix4fValue extends AbstractGLUniform<OpenMatrix4f> {

        private final OpenMatrix4f sharedValue = new OpenMatrix4f();
        private final FloatBuffer sharedBuffer = MatrixUtils.createFloatBuffer(16);

        protected Matrix4fValue(String name, Supplier<OpenMatrix4f> value) {
            super(name, value, new OpenMatrix4f());
        }

        @Override
        protected OpenMatrix4f upload(OpenMatrix4f newValue) {
            newValue.store(sharedBuffer);
            sharedValue.load(sharedBuffer);
            GL20.glUniformMatrix4fv(location, false, sharedBuffer);
            return sharedValue;
        }

        @Override
        protected OpenMatrix4f download() {
            GL20.glGetUniformfv(program, location, sharedBuffer);
            return new OpenMatrix4f(sharedBuffer);
        }
    }

    private static class Matrix3fValue extends AbstractGLUniform<OpenMatrix3f> {

        private final OpenMatrix3f sharedValue = new OpenMatrix3f();
        private final FloatBuffer sharedBuffer = MatrixUtils.createFloatBuffer(9);

        protected Matrix3fValue(String name, Supplier<OpenMatrix3f> value) {
            super(name, value, new OpenMatrix3f());
        }

        @Override
        protected OpenMatrix3f upload(OpenMatrix3f newValue) {
            newValue.store(sharedBuffer);
            sharedValue.load(sharedBuffer);
            GL20.glUniformMatrix3fv(location, false, sharedBuffer);
            return sharedValue;
        }

        @Override
        protected OpenMatrix3f download() {
            GL20.glGetUniformfv(program, location, sharedBuffer);
            return new OpenMatrix3f(sharedBuffer);
        }
    }
}
