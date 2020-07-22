package moe.plushie.armourers_workshop.client.render;

import java.awt.Rectangle;
import java.util.ArrayDeque;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ModRenderHelper {

    private static float lightX;
    private static float lightY;

    public static void disableLighting() {
        lightX = OpenGlHelper.lastBrightnessX;
        lightY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
    }

    public static void enableLighting() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
    }

    public static void setLightingForBlock(World world, BlockPos pos) {
        int i = world.getCombinedLight(pos, 0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
    }

    public static void setGLForSkinRender() {

    }

    public static void unsetGLForSkinRender() {

    }

    public static void setGLForSkinRenderGUI() {
        enableAlphaBlend();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
    }

    public static void unsetGLForSkinRenderGUI() {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void enableAlphaBlend() {
        enableAlphaBlend(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void enableAlphaBlend(int sfactor, int dfactor) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(sfactor, dfactor);
    }

    public static void disableAlphaBlend() {
        GlStateManager.disableBlend();
    }

    private static ArrayDeque<Rectangle> scissorList = new ArrayDeque<Rectangle>();

    public static void enableScissor(int x, int y, int width, int height, boolean scaled) {
        Rectangle cut = new Rectangle(x, y, width, height);
        Rectangle rec = scissorList.peek();
        if (rec != null) {
            int left = x;
            int right = x + width;
            int top = y;
            int bottom = y + height;

            if (left < rec.x) {
                left = rec.x;
            }
            if (right > rec.x + rec.width) {
                right = rec.x + rec.width;
            }

            if (top < rec.y) {
                top = rec.y;
            }
            if (bottom > rec.y + rec.height) {
                bottom = rec.y + rec.height;
            }

            if (right < left) {
                right = left;
            }
            if (bottom < top) {
                bottom = top;
            }

            cut = new Rectangle(left, top, right - left, bottom - top);
        }

        scissorList.push(cut);
        // enableScissorScaled(x, y, width, height);
        enableScissorScaled(cut.x, cut.y, cut.width, cut.height);
    }

    private static void enableScissorScaled(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        double scaledWidth = mc.displayWidth / sr.getScaledWidth_double();
        double scaledHeight = mc.displayHeight / sr.getScaledHeight_double();

        int xScaled = MathHelper.floor(x * scaledWidth);
        int yScaled = mc.displayHeight - MathHelper.floor(((double) y + (double) height) * scaledHeight);
        int widthScaled = MathHelper.floor(width * scaledWidth);
        int heightScaled = MathHelper.floor(height * scaledHeight);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(xScaled, yScaled, widthScaled, heightScaled);
    }

    public static void disableScissor() {
        scissorList.poll();
        Rectangle rec = scissorList.peek();
        if (rec != null) {
            enableScissorScaled(rec.x, rec.y, rec.width, rec.height);
        } else {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    public static Rectangle getScissor() {
        return scissorList.peek();
    }
}
