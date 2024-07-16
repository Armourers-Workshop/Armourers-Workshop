package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.Vector4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public abstract class ShaderUniform {

    protected final String name;

    protected final int program;
    protected final int location;

    ShaderUniform(String name, int program, int location) {
        this.name = name;
        this.program = program;
        this.location = location;
    }

    public abstract void apply();

    public void push() {
    }

    public void pop() {
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

    public interface Factory<T> {

        ShaderUniform create(String name, int program, int location, Supplier<T> value);
    }

    public static class Int extends ShaderUniform {

        private final Supplier<Integer> value;
        private final Stack<Integer> cachedValues = new Stack<>();
        private int cachedValue = 0;

        Int(String name, int program, int location, Supplier<Integer> value) {
            super(name, program, location);
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

    public static class Vec4f extends ShaderUniform {

        private final Supplier<Vector4f> value;
        private Vector4f cachedValue = Vector4f.ZERO;

        Vec4f(String name, int program, int location, Supplier<Vector4f> value) {
            super(name, program, location);
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

    public static class Matrix4f extends ShaderUniform {

        private final FloatBuffer buffer = ObjectUtils.createFloatBuffer(16);
        private final Supplier<OpenMatrix4f> value;
        private final OpenMatrix4f cachedValue = OpenMatrix4f.createScaleMatrix(0, 0, 0);

        Matrix4f(String name, int program, int location, Supplier<OpenMatrix4f> value) {
            super(name, program, location);
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

    public static class Matrix3f extends ShaderUniform {

        private final FloatBuffer buffer = ObjectUtils.createFloatBuffer(9);
        private final Supplier<OpenMatrix3f> value;
        private final OpenMatrix3f cachedValue = OpenMatrix3f.createScaleMatrix(0, 0, 0);

        Matrix3f(String name, int program, int location, Supplier<OpenMatrix3f> value) {
            super(name, program, location);
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

    public static class Loader {

        final int programId;
        final ArrayList<String> registeredNames = new ArrayList<>();
        final ArrayList<ShaderUniform> uniforms = new ArrayList<>();

        public Loader(int programId) {
            this.programId = programId;
            register("aw_MatrixFlags", RenderSystem::getExtendedMatrixFlags, Int::new);
            register("aw_OverlayTextureMatrix", RenderSystem::getExtendedOverlayTextureMatrix, Matrix4f::new);
            register("aw_LightmapTextureMatrix", RenderSystem::getExtendedLightmapTextureMatrix, Matrix4f::new);
            register("aw_TextureMatrix", RenderSystem::getExtendedTextureMatrix, Matrix4f::new);
            register("aw_NormalMatrix", RenderSystem::getExtendedNormalMatrix, Matrix3f::new);
            register("aw_ModelViewMat", RenderSystem::getExtendedModelViewMatrix, Matrix4f::new);
            register("aw_ColorModulator", RenderSystem::getExtendedColorModulator, Vec4f::new);
        }

        private <T> void register(String name, Supplier<T> supplier, Factory<T> factory) {
            int location = GL20.glGetUniformLocation(programId, name);
            if (location != -1) {
                uniforms.add(factory.create(name, programId, location, supplier));
                registeredNames.add(name);
            }
        }
    }
}
