package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourBlockData;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomArmour extends ModelBiped{
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    private final ModelRenderer main;
    
    public ModelCustomArmour() {
        textureWidth = 4;
        textureHeight = 4;
        
        main = new ModelRenderer(this, 0, 0);
        main.addBox(0F, 0F, 0F, 1, 1, 1);
        main.setRotationPoint(0, 0, 0);
    }
    
    public static double RadiansToDegrees(double angle) {
        return angle * (180.0 / Math.PI);
     }
     
    public void renderPart(ArrayList<CustomArmourBlockData> armourBlockData, float scale) {
         for (int i = 0; i < armourBlockData.size(); i++) {
             CustomArmourBlockData blockData = armourBlockData.get(i);
             if (!blockData.isGlowing()) {
                 renderArmourBlock(blockData.x, blockData.y, blockData.z, blockData.colour, scale);
             }
         }
         
         float lastBrightnessX = OpenGlHelper.lastBrightnessX;
         float lastBrightnessY = OpenGlHelper.lastBrightnessY;
         GL11.glDisable(GL11.GL_LIGHTING);
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
         
         for (int i = 0; i < armourBlockData.size(); i++) {
             CustomArmourBlockData blockData = armourBlockData.get(i);
             if (blockData.isGlowing()) {
                 renderArmourBlock(blockData.x, blockData.y, blockData.z, blockData.colour, scale);
             }
         }
         
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
         GL11.glEnable(GL11.GL_LIGHTING);
     }

     public void renderArmourBlock(int x, int y, int z, int colour, float scale) {
         float colourRed = (colour >> 16 & 0xff) / 255F;
         float colourGreen = (colour >> 8 & 0xff) / 255F;
         float colourBlue = (colour & 0xff) / 255F;

         GL11.glPushMatrix();
         GL11.glColor3f(colourRed, colourGreen, colourBlue);
         GL11.glTranslated(x * scale, y * scale, z * scale);
         main.render(scale);
         GL11.glPopMatrix();
     }

     public void bindArmourTexture() {
         Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
     }
}
