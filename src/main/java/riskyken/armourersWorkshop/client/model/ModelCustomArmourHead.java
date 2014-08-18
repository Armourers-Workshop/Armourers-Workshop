package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.customarmor.AbstractCustomArmour;
import riskyken.armourersWorkshop.common.customarmor.ArmourBlockData;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.armourersWorkshop.utils.ModLogger;

public class ModelCustomArmourHead extends ModelBiped {
    
    private ModelRenderer main;
    
    public ModelCustomArmourHead() {
        main = new ModelRenderer(this, 28, 20);
        main.addBox(0F, 0F, 0F, 1, 1, 1);
        main.setRotationPoint(0, 0, 0);
    }
    
    @Override
    public void render(Entity entity, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        AbstractCustomArmour armourData = ClientProxy.getPlayerCustomArmour(entity, ArmourerType.HEAD);
        ModLogger.log(armourData);
        if (armourData == null) { return; }

        ArrayList<ArmourBlockData> armourBlockData = armourData.getArmourData();
        bindPlayerTexture();
        
        GL11.glPushMatrix();
        
        for (int i = 0; i < armourBlockData.size(); i++) {
            ArmourBlockData blockData = armourBlockData.get(i);
            if (!blockData.glowing) {
                renderArmourPart(blockData.x, blockData.y, blockData.z, blockData.colour, scale);
            }
        }
        
        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        
        for (int i = 0; i < armourBlockData.size(); i++) {
            ArmourBlockData blockData = armourBlockData.get(i);
            if (blockData.glowing) {
                renderArmourPart(blockData.x, blockData.y, blockData.z, blockData.colour, scale);
            }
        }
        
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        GL11.glEnable(GL11.GL_LIGHTING);
        
        GL11.glPopMatrix();
    }
    
    private void renderArmourPart(int x, int y, int z, int colour, float scale) {
        float colourRed = (colour >> 16 & 0xff) / 255F;
        float colourGreen = (colour >> 8 & 0xff) / 255F;
        float colourBlue = (colour & 0xff) / 255F;

        GL11.glPushMatrix();

        GL11.glColor3f(colourRed, colourGreen, colourBlue);

        ModLogger.log(x + " " + y + " " + z);
        GL11.glTranslated(x * scale, y * scale, z * scale);
        main.render(scale);
        GL11.glPopMatrix();
    }

    private void bindPlayerTexture() {
        ResourceLocation playerSkin = Minecraft.getMinecraft().thePlayer.getLocationSkin();
        Minecraft.getMinecraft().getTextureManager().bindTexture(playerSkin);
    }
}
