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
    
    public static void enableScissorScaled(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        double scaledWidth = mc.displayWidth / sr.getScaledWidth_double();
        double scaledHeight = mc.displayHeight / sr.getScaledHeight_double();
        enableScissor(
                MathHelper.floor(x * scaledWidth),
                mc.displayHeight - MathHelper.floor(((double)y + (double)height) * scaledHeight),
                MathHelper.floor(width * scaledWidth),
                MathHelper.floor(height * scaledHeight));
    }
    
    private static ArrayDeque<Rectangle> scissorList = new ArrayDeque<Rectangle>();
    
    public static void enableScissor(int x, int y, int width, int height) {
        Rectangle cut = new Rectangle(x, y, width, height);
        
        Rectangle rec = scissorList.peek();
        if (rec != null) {
            int x1 = x;
            int x2 = x + width;
            int y1 = y;
            int y2 = y + height;
            
            if (x1 < rec.x) {
                x1 = rec.x;
            }
            if (x2 > rec.x + rec.width) {
                x2 = rec.x + rec.width;
            }
             
            if (y1 < rec.y) {
                y1 = rec.y;
            }
            if (y2 > rec.y + rec.height) {
                y2 = rec.y + rec.height;
            }
            
            if (x2 < x1) {
                x2 = x1;
            }
            if (y2 < y1) {
                y2 = y1;
            }
            
            cut = new Rectangle(x1, y1, x2 - x1, y2 - y1);
            //ModLogger.log(cut);
        }
        
        scissorList.push(cut);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(cut.x, cut.y, cut.width, cut.height);
    }
    
    public static void disableScissor() {
        scissorList.poll();

        Rectangle rec = scissorList.peek();
        if (rec != null) {
            GL11.glScissor(rec.x, rec.y, rec.width, rec.height);
        } else {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }
}
