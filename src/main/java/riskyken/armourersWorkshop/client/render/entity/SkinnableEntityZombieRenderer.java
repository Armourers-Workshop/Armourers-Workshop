package riskyken.armourersWorkshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import riskyken.armourersWorkshop.api.client.render.entity.ISkinnableEntityRenderer;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class SkinnableEntityZombieRenderer implements ISkinnableEntityRenderer {
    
    @Override
    public void render(EntityLivingBase entity, RendererLivingEntity renderer, double x, double y, double z, IEntityEquipment entityEquipment) {
        GL11.glPushMatrix();
        float scale = 0.0625F;
        
        GL11.glTranslated(x, y, z);
        GL11.glScalef(1, -1, -1);
        
        //-24.0F * f5 - 0.0078125F
        GL11.glTranslated(0, -entity.height + 4.7F * scale, 0);
        
        GL11.glRotatef(entity.renderYawOffset, 0, 1, 0);

        renderEquipmentType(entity, renderer, SkinTypeRegistry.skinHead, entityEquipment);
        renderEquipmentType(entity, renderer, SkinTypeRegistry.skinChest, entityEquipment);
        renderEquipmentType(entity, renderer, SkinTypeRegistry.skinLegs, entityEquipment);
        renderEquipmentType(entity, renderer, SkinTypeRegistry.skinSkirt, entityEquipment);
        renderEquipmentType(entity, renderer, SkinTypeRegistry.skinFeet, entityEquipment);
        
        GL11.glPopMatrix();
    }
    
    private void renderEquipmentType(EntityLivingBase entity, RendererLivingEntity renderer, ISkinType skinType, IEntityEquipment equipmentData) {
        if (equipmentData.haveEquipment(skinType, 0)) {
            ISkinPointer skinPointer = equipmentData.getSkinPointer(skinType, 0);
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
            
            if (renderer instanceof RenderZombie) {
                RenderZombie rz = (RenderZombie) renderer;
                boolean isZombieVillager = false;
                float scale = 0.0625F;
                
                isZombieVillager = skinType == SkinTypeRegistry.skinHead & rz.modelBipedMain instanceof ModelZombieVillager;
                if (isZombieVillager) {
                    GL11.glTranslated(0, -2.0F * scale, 0);
                }
                if (rz.modelBipedMain.isChild) {
                    GL11.glEnable(GL11.GL_NORMALIZE);
                }
                //EquipmentModelRenderer.INSTANCE.renderEquipmentPart(entity, rz.modelBipedMain, skin);
                if (rz.modelBipedMain.isChild) {
                    GL11.glDisable(GL11.GL_NORMALIZE);
                }
                if (isZombieVillager) {
                    GL11.glTranslated(0, 2.0F * scale, 0);
                }
            } else {
                //EquipmentModelRenderer.INSTANCE.renderEquipmentPart(entity, null, skin);
            }
        }
    }
}
