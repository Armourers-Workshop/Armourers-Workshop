package riskyken.armourersWorkshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import riskyken.armourersWorkshop.api.client.render.entity.ISkinnableEntityRenderer;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
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
        if (equipmentData.haveEquipment(skinType, 0)) {
            ISkinPointer skinPointer = equipmentData.getSkinPointer(skinType, 0);
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
            GL11.glEnable(GL11.GL_NORMALIZE);
            //EquipmentModelRenderer.INSTANCE.renderEquipmentPart(entity, null, skin);
            GL11.glDisable(GL11.GL_NORMALIZE);
        }
    }
}
