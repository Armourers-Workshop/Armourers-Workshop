package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.client.render.EquipmentRenderHelper;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentPartData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomArmourSkirt extends ModelCustomArmour {
    
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
        ApiRegistrar.INSTANCE.onRenderEquipment(entity, EnumEquipmentType.SKIRT);
        armourData.onRender();
        
        for (int i = 0; i < parts.size(); i++) {
            CustomEquipmentPartData part = parts.get(i);
            if (!part.facesBuild) {
                EquipmentRenderHelper.cullFacesOnEquipmentPart(part);
            }
            
            GL11.glPushMatrix();
            if (isChild) {
                float f6 = 2.0F;
                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
                GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
            }
            
            ApiRegistrar.INSTANCE.onRenderEquipmentPart(entity, part.getArmourPart());
            switch (part.getArmourPart()) {
            case SKIRT:
                renderSkirt(part, scale);
                break;
            default:
                break;
            }
            
            GL11.glPopMatrix();
        }
        
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderSkirt(CustomEquipmentPartData part, float scale) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
      
        GL11.glTranslated(0, 12 * scale, 0);
        
        if (isSneak) {
            GL11.glTranslated(0, -3 * scale, 4 * scale);
        }
        
        renderPart(part, scale);
        GL11.glPopMatrix();
    }
}
