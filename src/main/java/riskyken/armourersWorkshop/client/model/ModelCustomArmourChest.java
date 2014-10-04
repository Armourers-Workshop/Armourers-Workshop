package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourPartData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomArmourChest extends ModelCustomArmour {
    
    public void render(Entity entity, ModelBiped modelBiped, CustomArmourItemData armourData) {
        setRotationFromModelBiped(modelBiped);

        if (armourData == null) { return; }
        ArrayList<CustomArmourPartData> parts = armourData.getParts();
        
        if (entity != null) {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
            this.heldItemRight = 0;
            if (player.getHeldItem() != null) {
                this.heldItemRight = 1;
            }
        }
        
        bindArmourTexture();
        
        for (int i = 0; i < parts.size(); i++) {
            CustomArmourPartData part = parts.get(i);
            switch (part.getArmourPart()) {
            case CHEST:
                renderChest(part, scale);
                break;
            case LEFT_ARM:
                renderLeftArm(part, scale);
                break;
            case RIGHT_ARM:
                renderRightArm(part, scale);
                break;   
                
            default:
                break;
            }
        }
        
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderChest(CustomArmourPartData part, float scale) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        renderPart(part.getArmourData(), scale);
        GL11.glPopMatrix();
    }
    
    private void renderLeftArm(CustomArmourPartData part, float scale) {
        GL11.glPushMatrix();
        
        //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        
        GL11.glTranslatef(5.0F * scale, 2.0F  * scale, 0);
        
        GL11.glRotatef((float) RadiansToDegrees(this.bipedLeftArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) RadiansToDegrees(this.bipedLeftArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) RadiansToDegrees(this.bipedLeftArm.rotateAngleX), 1, 0, 0);
        
        renderPart(part.getArmourData(), scale);
        
        GL11.glPopMatrix();
    }
    
    private void renderRightArm(CustomArmourPartData part, float scale) {
        GL11.glPushMatrix();
        
        //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);
        GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        //GL11.glRotatef((float) RadiansToDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        
        GL11.glTranslatef(-5.0F * scale, 2.0F  * scale, 0);
        
        GL11.glRotatef((float) RadiansToDegrees(this.bipedRightArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) RadiansToDegrees(this.bipedRightArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) RadiansToDegrees(this.bipedRightArm.rotateAngleX), 1, 0, 0);
        
        renderPart(part.getArmourData(), scale);
        GL11.glPopMatrix();
    }
}
