package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.custom.equipment.data.CustomEquipmentBlockData;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomArmour extends ModelBiped{
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    protected static float scale = 0.0625F;
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
    
    protected void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
    
    protected void setRotation(ModelRenderer targetModel, ModelRenderer sourceModel) {
        targetModel.rotateAngleX = sourceModel.rotateAngleX;
        targetModel.rotateAngleY = sourceModel.rotateAngleY;
        targetModel.rotateAngleZ = sourceModel.rotateAngleZ;
    }
    
    protected void setRotationFromRender(RenderPlayer render) {
        setRotation(bipedHead, render.modelBipedMain.bipedHead);
        setRotation(bipedBody, render.modelBipedMain.bipedBody);
        setRotation(bipedLeftArm, render.modelBipedMain.bipedLeftArm);
        setRotation(bipedRightArm, render.modelBipedMain.bipedRightArm);
        setRotation(bipedLeftLeg, render.modelBipedMain.bipedLeftLeg);
        setRotation(bipedRightLeg, render.modelBipedMain.bipedRightLeg);
    }
     
    public void renderPart(ArrayList<CustomEquipmentBlockData> armourBlockData, float scale) {
         for (int i = 0; i < armourBlockData.size(); i++) {
             CustomEquipmentBlockData blockData = armourBlockData.get(i);
             if (!blockData.isGlowing()) {
                 renderArmourBlock(blockData.x, blockData.y, blockData.z, blockData.colour, scale);
             }
         }
         
         float lastBrightnessX = OpenGlHelper.lastBrightnessX;
         float lastBrightnessY = OpenGlHelper.lastBrightnessY;
         GL11.glDisable(GL11.GL_LIGHTING);
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
         
         for (int i = 0; i < armourBlockData.size(); i++) {
             CustomEquipmentBlockData blockData = armourBlockData.get(i);
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
     
     public double DegreesToRadians(double degrees) {
         return 2 * Math.PI * degrees / 360.0;
     }
     
     public void bindArmourTexture() {
         Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
     }
}
