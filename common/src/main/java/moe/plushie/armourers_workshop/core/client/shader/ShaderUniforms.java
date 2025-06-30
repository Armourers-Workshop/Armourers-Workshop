package moe.plushie.armourers_workshop.core.client.shader;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.init.ModLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ShaderUniforms {

    private static ShaderUniforms INSTANCE = new ShaderUniforms();

    private final Int2ObjectMap<State> states = new Int2ObjectOpenHashMap<>();

    public static ShaderUniforms getInstance() {
        return INSTANCE;
    }

    public static void begin() {
    }

    public static void end() {
        var instance = getInstance();
        var currentProgram = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        instance.states.forEach((programId, state) -> {
            if (!state.isChanged()) {
                return;
            }
            if (currentProgram != programId) {
                GL20.glUseProgram(programId);
            }
            state.reset();
            if (currentProgram != programId) {
                GL20.glUseProgram(currentProgram);
            }
        });
    }

    public static void clear() {
        // Ignore call when changes empty.
        if (getInstance().states.isEmpty()) {
            return;
        }
        INSTANCE = new ShaderUniforms();
        ModLog.debug("reset all uniforms from shader changes");
    }

    public void apply(int programId) {
        if (programId == 0) {
            return;
        }
        var state = states.computeIfAbsent(programId, State::new);
        if (!state.isEmpty()) {
            state.apply();
        }
    }

    private static class State {

        private final List<ShaderUniform> uniforms;
        private List<ShaderUniform> changes;

        public State(int program) {
            this.uniforms = Builder.of(program).built(ShaderContext.getInstance()).build();
        }

        public void apply() {
            if (changes == null) {
                uniforms.forEach(ShaderUniform::push);
                changes = uniforms;
            }
            uniforms.forEach(ShaderUniform::apply);
        }

        public void reset() {
            if (changes != null) {
                uniforms.forEach(ShaderUniform::pop);
                changes = null;
            }
        }

        public boolean isEmpty() {
            return uniforms.isEmpty();
        }

        public boolean isChanged() {
            return changes != null;
        }
    }

    private static class Builder {

        final int programId;
        final ArrayList<ShaderUniform> uniforms = new ArrayList<>();

        private Builder(int programId) {
            this.programId = programId;
        }

        public static Builder of(int programId) {
            return new Builder(programId);
        }

        public Builder built(ShaderContext context) {
            add("aw_MatrixFlags", context::matrixFlags, ShaderUniform.Int::new);
            add("aw_NormalMatrix", context::objectNormalMatrix, ShaderUniform.Matrix3f::new);
            add("aw_ModelViewMatrix", context::objectViewMatrix, ShaderUniform.Matrix4f::new);
            add("aw_OverlayTextureMatrix", context::overlayTextureMatrix, ShaderUniform.Matrix4f::new);
            add("aw_LightmapTextureMatrix", context::lightmapTextureMatrix, ShaderUniform.Matrix4f::new);
            add("aw_TextureMatrix", context::textureMatrix, ShaderUniform.Matrix4f::new);
            add("aw_ColorModulator", context::colorModulator, ShaderUniform.Vec4f::new);
            return this;
        }

        public <T> Builder add(String name, Supplier<T> supplier, Factory<T> factory) {
            int location = GL20.glGetUniformLocation(programId, name);
            if (location != -1) {
                uniforms.add(factory.create(name, programId, location, supplier));
            }
            return this;
        }

        public ArrayList<ShaderUniform> build() {
            return uniforms;
        }
    }

    private interface Factory<T> {

        ShaderUniform create(String name, int program, int location, Supplier<T> value);
    }
}
