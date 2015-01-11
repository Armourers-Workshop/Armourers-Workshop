package riskyken.armourersWorkshop.client;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.world.World;

/**
 * Helps turn Minecraft's lighting on and off.
 * Yip that's all!
 * @author RiskyKen
 * 
 */
public final class LightingHelper {
    
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
    
    public static void setLightingForBlock(World world, int x, int y, int z) {
        int i = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
    }
}
