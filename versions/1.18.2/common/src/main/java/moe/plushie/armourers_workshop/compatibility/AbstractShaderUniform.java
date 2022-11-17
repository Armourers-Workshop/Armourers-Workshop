package moe.plushie.armourers_workshop.compatibility;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Stack;
import java.util.function.Supplier;

public abstract class AbstractShaderUniform {

    protected final String name;

    protected final int program;
    protected final int location;

    AbstractShaderUniform(String name, int program, int location) {
        this.name = name;
        this.program = program;
        this.location = location;
    }

    public abstract void apply();

    public abstract void push();

    public abstract void pop();

    public interface Factory<T> {

        AbstractShaderUniform create(String name, int program, int location, Supplier<T> value);
    }

    public static class Matrix4fUniform extends AbstractShaderUniform {

        private final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        private final Stack<FloatBuffer> cachedBuffers = new Stack<>();
        private final Supplier<Matrix4f> value;

        private Matrix4f cachedValue;

        Matrix4fUniform(String name, int program, int location, Supplier<Matrix4f> value) {
            super(name, program, location);
            this.cachedValue = null;
            this.value = value;
        }

        @Override
        public void apply() {
            Matrix4f newValue = value.get();
            if (newValue != cachedValue) {
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
        }
    }

    public static class Matrix3fUniform extends AbstractShaderUniform {

        private final FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        private final Stack<FloatBuffer> cachedBuffers = new Stack<>();
        private final Supplier<Matrix3f> value;
        private Matrix3f cachedValue;

        Matrix3fUniform(String name, int program, int location, Supplier<Matrix3f> value) {
            super(name, program, location);
            this.cachedValue = null;
            this.value = value;
        }

        @Override
        public void apply() {
            Matrix3f newValue = value.get();
            if (newValue != cachedValue) {
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
        }
    }

    public static class Loader {

        final int programId;
        final ArrayList<String> registeredNames = new ArrayList<>();
        final ArrayList<AbstractShaderUniform> uniforms = new ArrayList<>();

        public Loader(int programId) {
            this.programId = programId;

            register("__aw__TextureMatrix", RenderSystem::getExtendedTextureMatrix, Matrix4fUniform::new);
            register("__aw__NormalMatrix", RenderSystem::getExtendedNormalMatrix, Matrix3fUniform::new);
            register("__aw__LightmapTextureMatrix", RenderSystem::getExtendedLightmapTextureMatrix, Matrix4fUniform::new);

            // optifine only!
            register("modelViewMatrix", RenderSystem::getExtendedModelViewMatrix, Matrix4fUniform::new);
            if (!registeredNames.contains("__aw__TextureMatrix")) {
                register("textureMatrix", RenderSystem::getExtendedTextureMatrix, Matrix4fUniform::new);
            }
            if (!registeredNames.contains("__aw__NormalMatrix")) {
                register("normalMatrix", RenderSystem::getExtendedNormalMatrix, Matrix3fUniform::new);
            }
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
