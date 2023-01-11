package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Stack;
import java.util.function.Supplier;

@Environment(value = EnvType.CLIENT)
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

    public abstract void push();

    public abstract void pop();

    public interface Factory<T> {

        ShaderUniform create(String name, int program, int location, Supplier<T> value);
    }

    public static class Matrix4f extends ShaderUniform {

        private final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        private final Stack<FloatBuffer> cachedBuffers = new Stack<>();
        private final Supplier<IMatrix4f> value;

        private IMatrix4f cachedValue;

        Matrix4f(String name, int program, int location, Supplier<IMatrix4f> value) {
            super(name, program, location);
            this.cachedValue = null;
            this.value = value;
        }

        @Override
        public void apply() {
            IMatrix4f newValue = value.get();
            if (!newValue.equals(cachedValue)) {
                cachedValue = newValue.copy();
                cachedValue.store(buffer);
                buffer.rewind();
                GL20.glUniformMatrix4fv(location, false, buffer);
            }
        }

        @Override
        public void push() {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
            GL20.glGetUniformfv(program, location, buffer);
            cachedBuffers.push(buffer);
        }

        @Override
        public void pop() {
            FloatBuffer buffer = cachedBuffers.pop();
            GL20.glUniformMatrix4fv(location, false, buffer);
            cachedValue = null;
        }
    }

    public static class Matrix3f extends ShaderUniform {

        private final FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        private final Stack<FloatBuffer> cachedBuffers = new Stack<>();
        private final Supplier<IMatrix3f> value;
        private IMatrix3f cachedValue;

        Matrix3f(String name, int program, int location, Supplier<IMatrix3f> value) {
            super(name, program, location);
            this.cachedValue = null;
            this.value = value;
        }

        @Override
        public void apply() {
            IMatrix3f newValue = value.get();
            if (!newValue.equals(cachedValue)) {
                cachedValue = newValue.copy();
                cachedValue.store(buffer);
                buffer.rewind();
                GL20.glUniformMatrix3fv(location, false, buffer);
            }
        }

        @Override
        public void push() {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
            GL20.glGetUniformfv(program, location, buffer);
            cachedBuffers.push(buffer);
        }

        @Override
        public void pop() {
            FloatBuffer buffer = cachedBuffers.pop();
            GL20.glUniformMatrix3fv(location, false, buffer);
            cachedValue = null;
        }
    }

    public static class Loader {

        final int programId;
        final ArrayList<String> registeredNames = new ArrayList<>();
        final ArrayList<ShaderUniform> uniforms = new ArrayList<>();

        public Loader(int programId) {
            this.programId = programId;
            register("aw_LightmapTextureMatrix", RenderSystem::getExtendedLightmapTextureMatrix, Matrix4f::new);
            register("aw_TextureMatrix", RenderSystem::getExtendedTextureMatrix, Matrix4f::new);
            register("aw_NormalMatrix", RenderSystem::getExtendedNormalMatrix, Matrix3f::new);
            register("aw_ModelViewMat", RenderSystem::getExtendedModelViewMatrix, Matrix4f::new);
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
