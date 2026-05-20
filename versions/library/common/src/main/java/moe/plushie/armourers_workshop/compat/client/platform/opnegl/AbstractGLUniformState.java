package moe.plushie.armourers_workshop.compat.client.platform.opnegl;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@OnlyIn(Dist.CLIENT)
public class AbstractGLUniformState {

    public final AtomicInteger matrixFlags = new AtomicInteger();
    public final AtomicInteger scissorFlags = new AtomicInteger();

    public final Storage<OpenMatrix3f> objectNormalMatrix = new Storage<>(OpenMatrix3f.createScaleMatrix(1, 1, 1));
    public final Storage<OpenMatrix4f> objectViewMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    public final Storage<OpenMatrix4f> textureMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    public final Storage<OpenMatrix4f> overlayTextureMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    public final Storage<OpenMatrix4f> lightmapTextureMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    public final Storage<OpenMatrix4f> modelViewMatrix = new Storage<>(null);
    public final Storage<OpenVector4f> colorModulator = new Storage<>(OpenVector4f.ONE);

    public final Int2ObjectOpenHashMap<List<AbstractGLUniform<?>>> values = new Int2ObjectOpenHashMap<>();

    public void pushMatrices() {
        objectNormalMatrix.save();
        objectViewMatrix.save();
        textureMatrix.save();
        overlayTextureMatrix.save();
        lightmapTextureMatrix.save();
        modelViewMatrix.save();
        colorModulator.save();
    }

    public void popMatrices() {
        objectNormalMatrix.restore();
        objectViewMatrix.restore();
        textureMatrix.restore();
        overlayTextureMatrix.restore();
        lightmapTextureMatrix.restore();
        modelViewMatrix.restore();
        colorModulator.restore();
    }

    public void pushVariables(int program) {
        get(program).forEach(AbstractGLUniform::push);
    }

    public void applyVariables(int program) {
        get(program).forEach(AbstractGLUniform::apply);
    }

    public void popVariables(int program) {
        get(program).forEach(AbstractGLUniform::pop);
    }

    public void invalidateVariables() {
        values.clear();
    }

    protected List<AbstractGLUniform<?>> get(int program) {
        var uniforms = values.get(program);
        if (uniforms != null) {
            return uniforms;
        }
        // create uniform and the link.
        uniforms = new ArrayList<>();
        uniforms.add(AbstractGLUniform.integer("aw_MatrixFlags", matrixFlags::get));
        uniforms.add(AbstractGLUniform.vector4f("aw_ColorModulator", colorModulator::get));
        uniforms.add(AbstractGLUniform.matrix3f("aw_NormalMatrix", objectNormalMatrix::get));
        uniforms.add(AbstractGLUniform.matrix4f("aw_ModelViewMatrix", objectViewMatrix::get));
        uniforms.add(AbstractGLUniform.matrix4f("aw_OverlayTextureMatrix", overlayTextureMatrix::get));
        uniforms.add(AbstractGLUniform.matrix4f("aw_LightmapTextureMatrix", lightmapTextureMatrix::get));
        uniforms.add(AbstractGLUniform.matrix4f("aw_TextureMatrix", textureMatrix::get));
        uniforms.removeIf(it -> {
            it.link(program);
            return !it.isLinked();
        });
        this.values.put(program, uniforms);
        return uniforms;
    }

    public static class Storage<T> {

        private T value;
        private T backup;

        public Storage(T value) {
            this.value = value;
            this.backup = value;
        }

        public void save() {
            backup = value;
        }

        public void restore() {
            value = backup;
        }

        public void set(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }
    }
}
