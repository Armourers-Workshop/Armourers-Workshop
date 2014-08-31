package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.customarmor.ArmourBlockData;
import riskyken.armourersWorkshop.common.customarmor.ArmourPart;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.CustomArmourData;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomArmourChest extends ModelBiped {

    private ModelRenderer main;
    private final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    
    public ModelCustomArmourChest() {
        textureWidth = 4;
        textureHeight = 4;
        
        main = new ModelRenderer(this, 0, 0);
        main.addBox(0F, 0F, 0F, 1, 1, 1);
        main.setRotationPoint(0, 0, 0);
    }

    @Override
    public void render(Entity entity, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        
        CustomArmourData chestData = ClientProxy.getPlayerCustomArmour(entity, ArmourerType.CHEST, ArmourPart.CHEST);
        CustomArmourData leftArmData = ClientProxy.getPlayerCustomArmour(entity, ArmourerType.CHEST, ArmourPart.LEFT_ARM);
        CustomArmourData rightArmData = ClientProxy.getPlayerCustomArmour(entity, ArmourerType.CHEST, ArmourPart.RIGHT_ARM);
        EntityPlayer player = (EntityPlayer) entity;
        
        this.isSneak = player.isSneaking();
        this.isRiding = player.isRiding();
        this.heldItemRight = 0;
        if (player.getHeldItem() != null) {
            this.heldItemRight = 1;
        }
        setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entity);
        
        bindArmourTexture();
        
        if (chestData != null){
            GL11.glPushMatrix();
            GL11.glColor3f(1F, 1F, 1F);
            
            GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
            GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
            GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
            
            GL11.glTranslated(0, -2 * scale, 0);
            renderPart(chestData.getArmourData(), scale);
            GL11.glPopMatrix();
        }
        
        
        if (leftArmData != null){
            GL11.glPushMatrix();
            
            //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
            GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
            //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
            
            GL11.glTranslatef(5.0F * scale, 2.0F  * scale, 0);
            
            GL11.glRotatef((float) RadiansToDegrees(this.bipedLeftArm.rotateAngleZ), 0, 0, 1);
            GL11.glRotatef((float) RadiansToDegrees(this.bipedLeftArm.rotateAngleY), 0, 1, 0);
            GL11.glRotatef((float) RadiansToDegrees(this.bipedLeftArm.rotateAngleX), 1, 0, 0);
            
            GL11.glTranslatef(1.0F * scale, -8.0F  * scale, 0);
            
            renderPart(leftArmData.getArmourData(), scale);
            
            GL11.glPopMatrix();
        }
        
        if (rightArmData != null){
            GL11.glPushMatrix();
            
            //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
            GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
            //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
            
            GL11.glTranslatef(-5.0F * scale, 2.0F  * scale, 0);
            
            GL11.glRotatef((float) RadiansToDegrees(this.bipedRightArm.rotateAngleZ), 0, 0, 1);
            GL11.glRotatef((float) RadiansToDegrees(this.bipedRightArm.rotateAngleY), 0, 1, 0);
            GL11.glRotatef((float) RadiansToDegrees(this.bipedRightArm.rotateAngleX), 1, 0, 0);
            
            GL11.glTranslatef(-1.0F * scale, -8.0F  * scale, 0);
            
            renderPart(rightArmData.getArmourData(), scale);
            GL11.glPopMatrix();
        }
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private static double RadiansToDegrees(double angle)
    {
       return angle * (180.0 / Math.PI);
    }
    
    private void renderPart(ArrayList<ArmourBlockData> armourBlockData, float scale) {
        for (int i = 0; i < armourBlockData.size(); i++) {
            ArmourBlockData blockData = armourBlockData.get(i);
            if (!blockData.glowing) {
                renderArmourBlock(blockData.x, blockData.y, blockData.z, blockData.colour, scale);
            }
        }
        
        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        
        for (int i = 0; i < armourBlockData.size(); i++) {
            ArmourBlockData blockData = armourBlockData.get(i);
            if (blockData.glowing) {
                renderArmourBlock(blockData.x, blockData.y, blockData.z, blockData.colour, scale);
            }
        }
        
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void renderArmourBlock(int x, int y, int z, int colour, float scale) {
        float colourRed = (colour >> 16 & 0xff) / 255F;
        float colourGreen = (colour >> 8 & 0xff) / 255F;
        float colourBlue = (colour & 0xff) / 255F;

        GL11.glPushMatrix();

        GL11.glColor3f(colourRed, colourGreen, colourBlue);

        // ModLogger.log(x + " " + y + " " + z);
        GL11.glTranslated(x * scale, y * scale, z * scale);
        main.render(scale);
        GL11.glPopMatrix();
    }

    private void bindArmourTexture() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
    }
}
