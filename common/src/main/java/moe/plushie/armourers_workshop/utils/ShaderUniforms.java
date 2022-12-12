package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.init.ModLog;
import org.lwjgl.opengl.GL20;

import java.util.Collection;
import java.util.HashMap;

public class ShaderUniforms {

    private static ShaderUniforms INSTANCE = new ShaderUniforms();

    private final HashMap<Integer, HashMap<String, ShaderUniform>> changes = new HashMap<>();
    private final HashMap<Integer, Collection<ShaderUniform>> uniforms = new HashMap<>();

    public static ShaderUniforms getInstance() {
        return INSTANCE;
    }

    public static void begin() {
    }

    public static void end() {
        getInstance().restoreUniforms();
    }

    public static void clear() {
        // Ignore call when changes empty.
        if (getInstance().uniforms.isEmpty()) {
            return;
        }
        INSTANCE = new ShaderUniforms();
        ModLog.debug("reset all uniforms from shader changes");
    }

    public void apply() {
        int programId = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        if (programId == 0) {
            return;
        }
        Collection<ShaderUniform> uniforms = this.uniforms.computeIfAbsent(programId, this::getLocations);
        if (uniforms.isEmpty()) {
            return;
        }
        for (ShaderUniform uniform : uniforms) {
            saveUniform(uniform);
            uniform.apply();
        }
    }

    private void saveUniform(ShaderUniform uniform) {
        changes.computeIfAbsent(uniform.program, key -> new HashMap<>()).computeIfAbsent(uniform.name, key -> {
            uniform.push();
            return uniform;
        });
    }

    private void restoreUniforms() {
        if (changes.isEmpty()) {
            return;
        }
        int currentProgram = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        changes.forEach((program, uniforms) -> {
            if (currentProgram != program) {
                GL20.glUseProgram(program);
            }
            uniforms.forEach((name, uniform) -> uniform.pop());
            if (currentProgram != program) {
                GL20.glUseProgram(currentProgram);
            }
        });
        changes.clear();
    }

    private Collection<ShaderUniform> getLocations(int programId) {
        return new ShaderUniform.Loader(programId).uniforms;
    }
}
