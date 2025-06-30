package moe.plushie.armourers_workshop.core.client.shader;

import moe.plushie.armourers_workshop.compatibility.client.AbstractModelViewStack;
import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.utils.RenderSystem;

import java.util.concurrent.atomic.AtomicInteger;

public class ShaderContext {

    private static final ShaderContext INSTANCE = new ShaderContext();

    private final AbstractModelViewStack modelViewStack = AbstractModelViewStack.getInstance();

    private final AtomicInteger matrixFlags = new AtomicInteger();
    private final AtomicInteger scissorFlags = new AtomicInteger();

    private final Storage<OpenMatrix3f> objectNormalMatrix = new Storage<>(OpenMatrix3f.createScaleMatrix(1, 1, 1));
    private final Storage<OpenMatrix4f> objectViewMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    private final Storage<OpenMatrix4f> textureMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    private final Storage<OpenMatrix4f> overlayTextureMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    private final Storage<OpenMatrix4f> lightmapTextureMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    private final Storage<OpenVector4f> colorModulator = new Storage<>(OpenVector4f.ONE);
    private final Storage<OpenMatrix4f> modelViewMatrix = new Storage<>(null);

    public static ShaderContext getInstance() {
        return INSTANCE;
    }

    public void setObjectNormalMatrix(OpenMatrix3f value) {
        objectNormalMatrix.set(value);
    }

    public OpenMatrix3f objectNormalMatrix() {
        return objectNormalMatrix.get();
    }

    public void setObjectViewMatrix(OpenMatrix4f value) {
        objectViewMatrix.set(value);
    }

    public OpenMatrix4f objectViewMatrix() {
        return objectViewMatrix.get();
    }

    public void setTextureMatrix(OpenMatrix4f value) {
        textureMatrix.set(value);
    }

    public OpenMatrix4f textureMatrix() {
        return textureMatrix.get();
    }

    public void setOverlayTextureMatrix(OpenMatrix4f value) {
        overlayTextureMatrix.set(value);
    }

    public OpenMatrix4f overlayTextureMatrix() {
        return overlayTextureMatrix.get();
    }

    public void setLightmapTextureMatrix(OpenMatrix4f value) {
        lightmapTextureMatrix.set(value);
    }

    public OpenMatrix4f lightmapTextureMatrix() {
        return lightmapTextureMatrix.get();
    }

    public void setColorModulator(OpenVector4f value) {
        colorModulator.set(value);
    }

    public OpenVector4f colorModulator() {
        return colorModulator.get();
    }


    public void setMatrixFlags(int options) {
        matrixFlags.set(options);
    }

    public int matrixFlags() {
        return matrixFlags.get();
    }

    public void setScissorFlags(int flags) {
        scissorFlags.set(flags);
    }

    public int scissorFlags() {
        return scissorFlags.get();
    }

    public void setColor(float r, float g, float b) {
        setColor(r, g, b, 1.0f);
    }

    public void setColor(float r, float g, float b, float a) {
        RenderSystem.setShaderColor(r, g, b, a);
    }

    public void setModelViewMatrix(OpenMatrix4f value) {
        modelViewMatrix.set(value);
        modelViewStack.last().set(value);
        modelViewStack.apply(); // submit to context
    }

    public OpenMatrix4f modelViewMatrix() {
        return modelViewMatrix.get();
    }


    public void polygonMode(int face, int mode) {
        RenderSystem.polygonMode(face, mode);
    }

    public void polygonOffset(float factor, float units) {
        RenderSystem.polygonOffset(factor, units);
    }

    public void enablePolygonOffset() {
        RenderSystem.enablePolygonOffset();
    }

    public void disablePolygonOffset() {
        RenderSystem.disablePolygonOffset();
    }

    public void saveState() {
        modelViewStack.pushMatrix();
        modelViewMatrix.save();
        textureMatrix.save();
        objectNormalMatrix.save();
        lightmapTextureMatrix.save();
        objectViewMatrix.save();
    }

    public void restoreState() {
        textureMatrix.load();
        objectNormalMatrix.load();
        lightmapTextureMatrix.load();
        objectViewMatrix.load();
        modelViewMatrix.load();
        modelViewStack.popMatrix();
    }

    private static class Storage<T> {

        private T value;
        private T backup;

        public Storage(T value) {
            this.value = value;
            this.backup = value;
        }

        public void save() {
            backup = value;
        }

        public void load() {
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
