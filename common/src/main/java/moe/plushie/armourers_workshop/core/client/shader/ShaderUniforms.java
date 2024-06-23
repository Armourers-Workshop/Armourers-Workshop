package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.init.ModLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ShaderUniforms {

    private static ShaderUniforms INSTANCE = new ShaderUniforms();

    private final HashMap<Integer, State> states = new HashMap<>();

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

    public void apply() {
        var programId = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        if (programId == 0) {
            return;
        }
        var state = states.computeIfAbsent(programId, State::new);
        if (!state.isEmpty()) {
            state.apply();
        }
    }

    public static class State {

        private final List<ShaderUniform> uniforms;
        private List<ShaderUniform> changes;

        public State(int program) {
            var loader = new ShaderUniform.Loader(program);
            this.uniforms = loader.uniforms;
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
}
