package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.customEquipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.customEquipment.data.CustomArmourPartData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomArmourSkirt extends ModelCustomArmour {
    
    public void render(Entity entity, ModelBiped modelBiped, CustomArmourItemData armourData) {
        setRotationFromModelBiped(modelBiped);
        if (armourData == null) { return; }
        
        ArrayList<CustomArmourPartData> parts = armourData.getParts();
        
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
        ApiRegistrar.INSTANCE.onRenderEquipment(entity, EnumArmourType.SKIRT);
        
        for (int i = 0; i < parts.size(); i++) {
            CustomArmourPartData part = parts.get(i);
            ApiRegistrar.INSTANCE.onRenderEquipmentPart(entity, part.getArmourPart());
            switch (part.getArmourPart()) {
            case SKIRT:
                renderSkirt(part, scale);
                break;
            default:
                break;
            }
        }
        
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderSkirt(CustomArmourPartData part, float scale) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
      
        GL11.glTranslated(0, 11 * scale, 0);
        
        if (isSneak) {
            GL11.glTranslated(0, -3 * scale, 4 * scale);
        }
        
        renderPart(part.getArmourData(), scale);
        GL11.glPopMatrix();
    }
}
