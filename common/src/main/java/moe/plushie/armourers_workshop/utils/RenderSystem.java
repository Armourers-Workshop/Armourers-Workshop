package moe.plushie.armourers_workshop.utils;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.math.IRectangle3i;
import moe.plushie.armourers_workshop.compatibility.client.AbstractRenderSystem;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public final class RenderSystem extends AbstractRenderSystem {

    private static final AtomicInteger extendedMatrixFlags = new AtomicInteger();

    private static final Storage<OpenMatrix3f> extendedNormalMatrix = new Storage<>(OpenMatrix3f.createScaleMatrix(1, 1, 1));
    private static final Storage<OpenMatrix4f> extendedTextureMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    private static final Storage<OpenMatrix4f> extendedLightmapTextureMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    private static final Storage<OpenMatrix4f> extendedModelViewMatrix = new Storage<>(OpenMatrix4f.createScaleMatrix(1, 1, 1));
    private static final Storage<PaintColor> extendedTintColor = new Storage<>(PaintColor.WHITE);

    private static final FloatBuffer BUFFER = ObjectUtils.createFloatBuffer(3);

    private static final byte[][][] FACE_MARK_TEXTURES = {
            // 0, 1(w), 2(h), 3(d)
            {{1, 3}, {1, 0}, {0, 0}, {0, 3}},
            {{1, 3}, {1, 0}, {0, 0}, {0, 3}},

            {{1, 2}, {1, 0}, {0, 0}, {0, 2}},
            {{1, 2}, {1, 0}, {0, 0}, {0, 2}},

            {{3, 2}, {3, 0}, {0, 0}, {0, 2}},
            {{3, 2}, {3, 0}, {0, 0}, {0, 2}},
    };

    private static final byte[][][] FACE_MARK_VERTEXES = new byte[][][]{
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}}, // -y
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},  // +y
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}}, // -z
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},  // +z
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}}, // -x
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},  // +x
    };

    private static final LinkedList<CGRect> clipBounds = new LinkedList<>();


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
        addClipRect(new CGRect(x, y, width, height));
    }

    public static void addClipRect(CGRect rect) {
        if (!clipBounds.isEmpty()) {
            CGRect rect1 = clipBounds.getLast();
            rect = rect.intersection(rect1);
        }
        clipBounds.add(rect);
        applyScissor(rect);
    }

    public static void removeClipRect() {
        if (clipBounds.isEmpty()) {
            return;
        }
        clipBounds.removeLast();
        if (!clipBounds.isEmpty()) {
            applyScissor(clipBounds.getLast());
        } else {
            disableScissor();
        }
    }

    public static void applyScissor(CGRect rect) {
        Window window = Minecraft.getInstance().getWindow();
        double scale = window.getGuiScale();
        double sx = rect.getX() * scale;
        double sy = window.getHeight() - rect.getMaxY() * scale;
        double sw = rect.getWidth() * scale;
        double sh = rect.getHeight() * scale;
        enableScissor((int) sx, (int) sy, (int) sw, (int) sh);
    }

    public static boolean inScissorRect(CGRect rect1) {
        if (!clipBounds.isEmpty()) {
            CGRect rect = clipBounds.getLast();
            return rect.intersects(rect1);
        }
        return true;
    }

    public static boolean inScissorRect(int x, int y, int width, int height) {
        if (!clipBounds.isEmpty()) {
            CGRect rect = clipBounds.getLast();
            return rect.intersects(x, y, width, height);
        }
        return true;
    }

    public static void drawCube(PoseStack poseStack, IRectangle3i rect, float r, float g, float b, float a, MultiBufferSource buffers) {
        float x = rect.getMinX();
        float y = rect.getMinY();
        float z = rect.getMinZ();
        float w = rect.getWidth();
        float h = rect.getHeight();
        float d = rect.getDepth();
        drawCube(poseStack, x, y, z, w, h, d, r, g, b, a, buffers);
    }

    public static void drawCube(PoseStack poseStack, IRectangle3f rect, float r, float g, float b, float a, MultiBufferSource buffers) {
        float x = rect.getMinX();
        float y = rect.getMinY();
        float z = rect.getMinZ();
        float w = rect.getWidth();
        float h = rect.getHeight();
        float d = rect.getDepth();
        drawCube(poseStack, x, y, z, w, h, d, r, g, b, a, buffers);
    }

    public static void drawCube(PoseStack poseStack, float x, float y, float z, float w, float h, float d, float r, float g, float b, float a, MultiBufferSource buffers) {
        if (w == 0 || h == 0 || d == 0) {
            return;
        }
        PoseStack.Pose pose = poseStack.last();
        SkinVertexBufferBuilder builder1 = SkinVertexBufferBuilder.getBuffer(buffers);
        VertexConsumer builder = builder1.getBuffer(SkinRenderType.IMAGE_GUIDE);
        for (Direction dir : Direction.values()) {
            drawFace(pose, dir, x, y, z, w, h, d, 0, 0, r, g, b, a, builder);
        }
    }

    public static void drawFace(PoseStack.Pose pose, Direction dir, float x, float y, float z, float w, float h, float d, float u, float v, float r, float g, float b, float a, VertexConsumer builder) {
        byte[][] vertexes = FACE_MARK_VERTEXES[dir.get3DDataValue()];
        byte[][] textures = FACE_MARK_TEXTURES[dir.get3DDataValue()];
        float[] values = {0, w, h, d};
        for (int i = 0; i < 4; ++i) {
            builder.vertex(pose.pose(), x + vertexes[i][0] * w, y + vertexes[i][1] * h, z + vertexes[i][2] * d)
                    .color(r, g, b, a)
                    .uv(u + values[textures[i][0]], v + values[textures[i][1]])
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(0xf000f0)
                    .endVertex();
        }
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
            if (!isOnRenderThread()) {
                recordRenderCall(() -> this.value = value);
            } else {
                this.value = value;
            }
        }

        public T get() {
            assertOnRenderThread();
            return value;
        }
    }
}
