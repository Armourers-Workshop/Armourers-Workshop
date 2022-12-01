package moe.plushie.armourers_workshop.utils;

import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.HashMap;

public class ShaderUniforms {

    private static final HashMap<Integer, HashMap<String, ShaderUniform>> CHANGED = new HashMap<>();

    private final HashMap<Integer, ArrayList<ShaderUniform>> loadedUniforms = new HashMap<>();

    public static void save(ShaderUniform uniform) {
        CHANGED.computeIfAbsent(uniform.program, key -> new HashMap<>()).computeIfAbsent(uniform.name, key -> {
            uniform.push();
            return uniform;
        });
    }

    public static void rollback() {
        int currentProgram = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        CHANGED.forEach((program, uniforms) -> {
            if (currentProgram != program) {
                GL20.glUseProgram(program);
            }
            uniforms.forEach((name, uniform) -> uniform.pop());
            if (currentProgram != program) {
                GL20.glUseProgram(currentProgram);
            }
        });
        CHANGED.clear();
    }

    public void apply() {
        int programId = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        if (programId == 0) {
            return;
        }
        ArrayList<ShaderUniform> uniforms = loadedUniforms.computeIfAbsent(programId, this::getLocations);
        if (uniforms.isEmpty()) {
            return;
        }
        for (ShaderUniform uniform : uniforms) {
            save(uniform);
            uniform.apply();
        }
    }

    public void clear() {
//        if (lastUniforms == null || lastProgramId == 0) {
//            return;
//        }
//        int programId = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
//        if (programId != lastProgramId) {
//            GL20.glUseProgram(lastProgramId);
//        }
//        for (AbstractShaderUniform uniform : lastUniforms) {
////            uniform.clear();
//        }
//        if (programId != lastProgramId) {
//            GL20.glUseProgram(programId);
//        }
//        lastUniforms = null;
//        lastProgramId = 0;
    }

    private ArrayList<ShaderUniform> getLocations(int programId) {
        return new ShaderUniform.Loader(programId).uniforms;
    }



//    public static class Matrix4Uniform extends AbstractShaderUniform {
//
//        private final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
//        private final Matrix4f defaultValue = Matrix4f.createScaleMatrix(1, 1, 1);
//        private Matrix4f cachedValue;
//        private final Supplier<Matrix4f> value;
//
//        Matrix4Uniform(String name, int location, Supplier<Matrix4f> value) {
//            super(name, location);
//            this.cachedValue = null;
//            this.value = value;
//        }
//
//        @Override
//        public void apply() {
//            upload(value.get());
//        }
//
//        @Override
//        public void clear() {
//            upload(defaultValue);
//        }
//
//        private void upload(Matrix4f newValue) {
//            if (newValue != cachedValue) {
//                cachedValue = newValue.copy();
//                cachedValue.store(buffer);
//                buffer.rewind();
//                GL20.glUniformMatrix4fv(location, false, buffer);
//            }
//        }
//    }
//
//    public static class Matrix4Uniform2 extends AbstractShaderUniform {
//
//        private final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
//        private final FloatBuffer buffer2 = BufferUtils.createFloatBuffer(16);
//        private Matrix4f cachedValue;
//        private final Supplier<Matrix4f> value;
//
//        Matrix4Uniform2(String name, int location, Supplier<Matrix4f> value) {
//            super(name, location);
//            this.cachedValue = null;
//            this.value = value;
//        }
//
//        @Override
//        public void apply() {
//            cachedValue = value.get();
//            int programId = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
//            GL20.glGetUniformfv(programId, location, buffer2);
//            cachedValue.store(buffer);
//            buffer.rewind();
//            GL20.glUniformMatrix4fv(location, false, buffer);
//        }
//
//        @Override
//        public void clear() {
//            if (cachedValue == null) {
//                return;
//            }
//            GL20.glUniformMatrix4fv(location, false, buffer2);
//            cachedValue = null;
//        }
//    }
//
//    public static class Matrix3Uniform2 extends AbstractShaderUniform {
//
//        private final FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
//        private final FloatBuffer buffer2 = BufferUtils.createFloatBuffer(9);
//        private Matrix3f cachedValue;
//        private final Supplier<Matrix3f> value;
//
//        Matrix3Uniform2(String name, int location, Supplier<Matrix3f> value) {
//            super(name, location);
//            this.cachedValue = null;
//            this.value = value;
//        }
//
//        @Override
//        public void apply() {
//            cachedValue = value.get();
//            int programId = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
//            GL20.glGetUniformfv(programId, location, buffer2);
//            cachedValue.store(buffer);
//            buffer.rewind();
//            GL20.glUniformMatrix3fv(location, false, buffer);
//        }
//
//        @Override
//        public void clear() {
//            if (cachedValue == null) {
//                return;
//            }
//            GL20.glUniformMatrix3fv(location, false, buffer2);
//            cachedValue = null;
//        }
//    }
//
//
//
//    public static class Matrix3Uniform extends AbstractShaderUniform {
//
//        private final FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
//        private final Matrix3f defaultValue = Matrix3f.createScaleMatrix(1, 1, 1);
//        private Matrix3f cachedValue;
//        private final Supplier<Matrix3f> value;
//
//        Matrix3Uniform(String name, int location, Supplier<Matrix3f> value) {
//            super(name, location);
//            this.cachedValue = null;
//            this.value = value;
//        }
//
//        @Override
//        public void apply() {
//            upload(value.get());
//        }
//
//        @Override
//        public void clear() {
//            upload(defaultValue);
//        }
//
//        private void upload(Matrix3f newValue) {
//            if (newValue != cachedValue) {
//                cachedValue = newValue.copy();
//                cachedValue.store(buffer);
//                buffer.rewind();
//                GL20.glUniformMatrix3fv(location, false, buffer);
//            }
//        }
//    }
}
