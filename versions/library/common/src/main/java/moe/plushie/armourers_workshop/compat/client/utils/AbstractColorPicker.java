package moe.plushie.armourers_workshop.compat.client.utils;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.utils.MatrixUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.function.IntConsumer;

@Available("[1.16, )")
@OnlyIn(Dist.CLIENT)
public class AbstractColorPicker {

    private static final FloatBuffer BUFFER = MatrixUtils.createFloatBuffer(3);

    public static void pick(float x, float y, IntConsumer consumer) {
        var window = Minecraft.getInstance().getWindow();
        var guiScale = window.getGuiScale();
        var sx = (int) (x * guiScale);
        var sy = (int) ((window.getGuiScaledHeight() - y) * guiScale);
        BUFFER.rewind();
        GL11.glReadPixels(sx, sy, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, BUFFER);
        GL11.glFinish();
        var r = Math.round(BUFFER.get() * 255);
        var g = Math.round(BUFFER.get() * 255);
        var b = Math.round(BUFFER.get() * 255);
        consumer.accept(0xff000000 | r << 16 | g << 8 | b);
    }
}
