package moe.plushie.armourers_workshop.utils;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.ClipContextImpl;
import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.platform.Window;
import moe.plushie.armourers_workshop.compatibility.client.AbstractRenderSystem;
import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.MatrixUtils;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public final class RenderSystem extends AbstractRenderSystem {

    private static final AtomicInteger extendedScissorFlags = new AtomicInteger();

    private static final FloatBuffer BUFFER = MatrixUtils.createFloatBuffer(3);

    public static void safeCall(Runnable task) {
        if (isOnRenderThread()) {
            task.run();
        } else {
            recordRenderCall(task::run);
        }
    }

    public static int getPixelColor(float x, float y) {
        Window window = EnvironmentManager.getClient().getWindow();
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
        setShaderColor(color.red() / 255f, color.green() / 255f, color.blue() / 255f, color.alpha() / 255f);
    }

    public static void setShaderColor(SkinPaintColor color) {
        setShaderColor(color.red() / 255f, color.green() / 255f, color.blue() / 255f, 1.0f);
    }

    public static void setShaderColor(float f, float g, float h) {
        setShaderColor(f, g, h, 1.0f);
    }


    public static void setExtendedScissorFlags(int flags) {
        extendedScissorFlags.set(flags);
    }

    public static int getExtendedScissorFlags() {
        return extendedScissorFlags.get();
    }
}
