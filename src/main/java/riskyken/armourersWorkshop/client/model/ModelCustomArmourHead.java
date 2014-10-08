package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentType;
import riskyken.armourersWorkshop.client.render.EquipmentRenderHelper;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.equipment.data.CustomArmourPartData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentBlockData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomArmourHead extends ModelCustomArmour {
    
    public void render(Entity entity, ModelBiped modelBiped, CustomArmourItemData armourData) {
        setRotationFromModelBiped(modelBiped);
        if (armourData == null) { return; }
        
        ArrayList<CustomArmourPartData> parts = armourData.getParts();
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
        GL11.glRotated(RadiansToDegrees(bipedHead.rotateAngleY), 0, 1, 0);
        GL11.glRotated(RadiansToDegrees(bipedHead.rotateAngleX), 1, 0, 0);
        
        if (isSneak) {
            GL11.glTranslated(0, 1 * scale, 0);
        }
        
        renderHead(armourData.getParts().get(0), scale);
        
        GL11.glPopMatrix();
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderHead(CustomArmourPartData part, float scale) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        renderPart(part, scale);
        GL11.glPopMatrix();
    }
}
