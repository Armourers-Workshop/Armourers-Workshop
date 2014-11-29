package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.client.render.EquipmentRenderHelper;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentBlockData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentPartData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomArmourHead extends ModelCustomArmour {
    
    public void render(Entity entity, CustomEquipmentItemData armourData, float limb1, float limb2, float limb3, float headY, float headX) {
        setRotationAngles(limb1, limb2, limb3, headY, headX, scale, entity);
        render(entity, armourData);
    }
    
    public void render(Entity entity, ModelBiped modelBiped, CustomEquipmentItemData armourData) {
        setRotationFromModelBiped(modelBiped);
        render(entity, armourData);
    }
    
    private void render(Entity entity, CustomEquipmentItemData armourData) {
        if (armourData == null) { return; }
        
        ArrayList<CustomEquipmentPartData> parts = armourData.getParts();
        ArrayList<CustomEquipmentBlockData> armourBlockData = armourData.getParts().get(0).getArmourData();
        
        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
            this.heldItemRight = 0;
            if (player.getHeldItem() != null) {
                this.heldItemRight = 1;
            }
        }
        
        bindArmourTexture();
        ApiRegistrar.INSTANCE.onRenderEquipment(entity, EnumEquipmentType.HEAD);
        ApiRegistrar.INSTANCE.onRenderEquipmentPart(entity, EnumEquipmentPart.HEAD);
        armourData.onRender();
        
        if (!armourData.getParts().get(0).facesBuild) {
            EquipmentRenderHelper.cullFacesOnEquipmentPart(armourData.getParts().get(0));
        }
        
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glRotated(RadiansToDegrees(bipedHead.rotateAngleZ), 0, 0, 1);
        GL11.glRotated(RadiansToDegrees(bipedHead.rotateAngleY), 0, 1, 0);
        GL11.glRotated(RadiansToDegrees(bipedHead.rotateAngleX), 1, 0, 0);
        
        if (isSneak) {
            GL11.glTranslated(0, 1 * scale, 0);
        }
        
        renderHead(armourData.getParts().get(0), scale);
        
        GL11.glPopMatrix();
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderHead(CustomEquipmentPartData part, float scale) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        renderPart(part, scale);
        GL11.glPopMatrix();
    }
}
