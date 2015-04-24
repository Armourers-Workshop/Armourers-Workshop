package riskyken.armourersWorkshop.client.render.entity;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.client.render.entity.ISkinnableEntityRenderer;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.equipment.ClientEquipmentModelCache;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class SkinnableEntityChickenRenderer implements ISkinnableEntityRenderer {

    @Override
    public void render(EntityLivingBase entity, RendererLivingEntity renderer,
            double x, double y, double z, IEntityEquipment entityEquipment) {
        GL11.glPushMatrix();
        float scale = 0.0625F;
        
        GL11.glTranslated(x, y, z);
        GL11.glScalef(1, -1, -1);
        
        GL11.glRotatef(entity.renderYawOffset, 0, 1, 0);
        
        //-24.0F * f5 - 0.0078125F
        
        GL11.glTranslated(0, -9F * scale, 0);
        //GL11.glTranslated(-1.7F * scale, 0 , 0);
        GL11.glTranslated(0, 0, -4.0F * scale);
        
        GL11.glRotatef(entity.rotationYawHead - entity.renderYawOffset, 0, 1, 0);
        
        
        GL11.glRotatef(entity.rotationPitch, 1, 0, 0);
        
        
        
        float headScale = 0.5F;
        GL11.glScalef(headScale, headScale * 1.5F, headScale);
        
        renderEquipmentType(entity, renderer, SkinTypeRegistry.skinHead, entityEquipment);
        
        GL11.glPopMatrix();
    }
    
    private void renderEquipmentType(EntityLivingBase entity, RendererLivingEntity renderer, ISkinType skinType, IEntityEquipment equipmentData) {
        if (equipmentData.haveEquipment(skinType)) {
            int id = equipmentData.getEquipmentId(skinType);
            Skin skin = ClientEquipmentModelCache.INSTANCE.getEquipmentItemData(id);
            GL11.glEnable(GL11.GL_NORMALIZE);
            EquipmentModelRenderer.INSTANCE.renderEquipmentPart(entity, null, skin);
            GL11.glDisable(GL11.GL_NORMALIZE);
        }
    }
}
