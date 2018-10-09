package moe.plushie.armourers_workshop.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.MathHelper;
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
    /*
    public static void setLightingForBlock(World world, int x, int y, int z) {
        
        int i = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
        
    }
    */
    
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
    
    public static void enableScissorScaled(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        double scaledWidth = (double)mc.displayWidth / sr.getScaledWidth_double();
        double scaledHeight = (double)mc.displayHeight / sr.getScaledHeight_double();
        enableScissor(
                MathHelper.floor((double)x * scaledWidth),
                mc.displayHeight - MathHelper.floor((double)((double)y + (double)height) * scaledHeight),
                MathHelper.floor((double)width * scaledWidth),
                MathHelper.floor((double)height * scaledHeight));
    }
    
    public static void enableScissor(int x, int y, int width, int height) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x, y, width, height);
    }
    
    public static void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
