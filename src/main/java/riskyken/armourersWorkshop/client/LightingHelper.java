package riskyken.armourersWorkshop.client;

import net.minecraft.client.renderer.OpenGlHelper;

import org.lwjgl.opengl.GL11;

public final class LightingHelper {
    
    private static float lightX;
    private static float lightY;
    
    public static void disableLighting() {
        lightX = OpenGlHelper.lastBrightnessX;
        lightY = OpenGlHelper.lastBrightnessY;
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
    }
    
    public static void enableLighting() {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
