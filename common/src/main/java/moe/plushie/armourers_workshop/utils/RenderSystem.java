package moe.plushie.armourers_workshop.utils;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.ClipContextImpl;
import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.platform.Window;
import moe.plushie.armourers_workshop.compatibility.client.AbstractRenderSystem;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.Vector4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public final class RenderSystem extends AbstractRenderSystem {

    private static final AtomicInteger extendedMatrixFlags = new AtomicInteger();
    private static final AtomicInteger extendedScissorFlags = new AtomicInteger();

    private static final Storage<OpenMatrix3f> extendedNormalMatrix = new Storage<>(OpenMatrix3f.createScaleMatrix(1, 1, 1));
    private static final Storage<OpenMatrix4f> extendedTextureMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    private static final Storage<OpenMatrix4f> extendedOverlayTextureMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    private static final Storage<OpenMatrix4f> extendedLightmapTextureMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    private static final Storage<OpenMatrix4f> extendedModelViewMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    private static final Storage<Vector4f> extendedColorModulator = new Storage<>(Vector4f.ONE);
    private static final Storage<PaintColor> extendedTintColor = new Storage<>(PaintColor.WHITE);

    private static final Storage<Float> extendedFogStart = new Storage<>(0.0f);
    private static final Storage<Float> extendedFogEnd = new Storage<>(0.0f);

    private static final FloatBuffer BUFFER = ObjectUtils.createFloatBuffer(3);

    public static void call(Runnable task) {
        if (isOnRenderThread()) {
            task.run();
        } else {
            recordRenderCall(task::run);
        }
    }

    public static int getPixelColor(float x, float y) {
        Window window = Minecraft.getInstance().getWindow();
        double guiScale = window.getGuiScale();
        int sx = (int) (x * guiScale);
        int sy = (int) ((window.getGuiScaledHeight() - y) * guiScale);
        BUFFER.rewind();
        GL11.glReadPixels(sx, sy, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, BUFFER);
        GL11.glFinish();
        int r = Math.round(BUFFER.get() * 255);
        int g = Math.round(BUFFER.get() * 255);
        int b = Math.round(BUFFER.get() * 255);
        return 0xff000000 | r << 16 | g << 8 | b;
    }

    public static void addClipRect(int x, int y, int width, int height) {
        ClipContextImpl.getInstance().addClip(new ClipContextImpl.Rectangle(new CGRect(x, y, width, height)));
    }

    public static void removeClipRect() {
        ClipContextImpl.getInstance().removeClip();
    }

    public static void setShaderColor(UIColor color) {
        setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public static void setShaderColor(PaintColor color) {
        setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1.0f);
    }

    public static void setShaderColor(float f, float g, float h) {
        setShaderColor(f, g, h, 1.0f);
    }


    public static Vector4f getExtendedColorModulator() {
        return extendedColorModulator.get();
    }

    public static void setExtendedColorModulator(Vector4f value) {
        extendedColorModulator.set(value);
    }

    public static OpenMatrix3f getExtendedNormalMatrix() {
        return extendedNormalMatrix.get();
    }

    public static void setExtendedNormalMatrix(OpenMatrix3f value) {
        extendedNormalMatrix.set(value);
    }

    public static OpenMatrix4f getExtendedTextureMatrix() {
        return extendedTextureMatrix.get();
    }

    public static void setExtendedTextureMatrix(OpenMatrix4f value) {
        extendedTextureMatrix.set(value);
    }

    public static OpenMatrix4f getExtendedOverlayTextureMatrix() {
        return extendedOverlayTextureMatrix.get();
    }

    public static void setExtendedOverlayTextureMatrix(OpenMatrix4f value) {
        extendedOverlayTextureMatrix.set(value);
    }

    public static OpenMatrix4f getExtendedLightmapTextureMatrix() {
        return extendedLightmapTextureMatrix.get();
    }

    public static void setExtendedLightmapTextureMatrix(OpenMatrix4f value) {
        extendedLightmapTextureMatrix.set(value);
    }

    public static OpenMatrix4f getExtendedModelViewMatrix() {
        return extendedModelViewMatrix.get();
    }

    public static void setExtendedModelViewMatrix(OpenMatrix4f value) {
        extendedModelViewMatrix.set(value);
    }

    public static void setExtendedMatrixFlags(int options) {
        extendedMatrixFlags.set(options);
    }

    public static int getExtendedMatrixFlags() {
        return extendedMatrixFlags.get();
    }

    public static void setExtendedScissorFlags(int flags) {
        extendedScissorFlags.set(flags);
    }

    public static int getExtendedScissorFlags() {
        return extendedScissorFlags.get();
    }

    public static void setExtendedTintColor(PaintColor tintColor) {
        extendedTintColor.set(tintColor);
    }

    public static PaintColor getExtendedTintColor() {
        return extendedTintColor.get();
    }

    public static void backupExtendedMatrix() {
        extendedTextureMatrix.save();
        extendedNormalMatrix.save();
        extendedLightmapTextureMatrix.save();
        extendedModelViewMatrix.save();
        extendedTintColor.save();
    }

    public static void restoreExtendedMatrix() {
        extendedTextureMatrix.load();
        extendedNormalMatrix.load();
        extendedLightmapTextureMatrix.load();
        extendedModelViewMatrix.load();
        extendedTintColor.load();
    }

    public static void backupExtendedFog() {
        extendedFogStart.value = getShaderFogStart();
        extendedFogEnd.value = getShaderFogEnd();
    }

    public static void restoreExtendedFog() {
        setShaderFogStart(extendedFogStart.value);
        setShaderFogEnd(extendedFogEnd.value);
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
