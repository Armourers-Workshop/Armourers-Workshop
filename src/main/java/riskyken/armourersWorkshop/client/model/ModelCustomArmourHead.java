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

public class ModelCustomArmourHead extends ModelBiped {
    
    private ModelRenderer main;
    private final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    
    public ModelCustomArmourHead() {
        textureWidth = 4;
        textureHeight = 4;
        
        main = new ModelRenderer(this, 0, 0);
        main.addBox(0F, 0F, 0F, 1, 1, 1);
        main.setRotationPoint(0, 0, 0);
    }
    
    @Override
    public void render(Entity entity, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        //setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entity);
        CustomArmourData armourData = ClientProxy.getPlayerCustomArmour(entity, ArmourerType.HEAD, ArmourPart.HEAD);
        //
        if (armourData == null) { return; }

        EntityPlayer player = (EntityPlayer) entity;
        
        
        ArrayList<ArmourBlockData> armourBlockData = armourData.getArmourData();
        
        this.isSneak = player.isSneaking();
        this.isRiding = player.isRiding();
        this.heldItemRight = 0;
        if (player.getHeldItem() != null) {
            this.heldItemRight = 1;
        }
        
        bindArmourTexture();
        
        GL11.glPushMatrix();
        GL11.glColor3f(0F, 0F, 0F);
        
        GL11.glRotatef(p_78088_5_, 0, 1, 0);
        GL11.glRotatef(p_78088_6_, 1, 0, 0);
        
        GL11.glTranslated(0, -18 * scale, 0);
        
        if (isSneak) {
            GL11.glTranslated(0, 1 * scale, 0);
        }
        
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
        
        GL11.glPopMatrix();
    }
    
    private void renderArmourBlock(int x, int y, int z, int colour, float scale) {
        float colourRed = (colour >> 16 & 0xff) / 255F;
        float colourGreen = (colour >> 8 & 0xff) / 255F;
        float colourBlue = (colour & 0xff) / 255F;

        GL11.glPushMatrix();
        GL11.glColor3f(colourRed, colourGreen, colourBlue);
        GL11.glTranslated(x * scale, y * scale, z * scale);
        main.render(scale);
        GL11.glPopMatrix();
    }

    private void bindArmourTexture() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
    }
}
