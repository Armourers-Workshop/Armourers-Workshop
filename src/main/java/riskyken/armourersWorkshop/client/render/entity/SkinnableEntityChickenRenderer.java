package riskyken.armourersWorkshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import riskyken.armourersWorkshop.api.client.render.entity.ISkinnableEntityRenderer;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.handler.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.render.SkinPartRenderer;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

@SideOnly(Side.CLIENT)
public class SkinnableEntityChickenRenderer implements ISkinnableEntityRenderer<EntityChicken> {

    @Override
    public void render(EntityChicken entity, RendererLivingEntity renderer,
            double x, double y, double z, IEntityEquipment entityEquipment) {
        GL11.glPushMatrix();
        float scale = 0.0625F;
        
        GL11.glTranslated(x, y, z);
        GL11.glScalef(1, -1, -1);
        
        double rot = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * ModClientFMLEventHandler.renderTickTime;
        GL11.glRotated(rot, 0, 1, 0);
        
        if (entity.deathTime > 0) {
            float angle = ((float)entity.deathTime + ModClientFMLEventHandler.renderTickTime - 1.0F) / 20.0F * 1.6F;
            angle = MathHelper.sqrt_float(angle);
            if (angle > 1.0F) {
                angle = 1.0F;
            }
            GL11.glRotatef(angle * 90F, 0.0F, 0.0F, 1.0F);
        }
        
        if (entity.isChild()) {
            GL11.glTranslated(0, scale * 5, scale * 1.5F);
        }
        
        GL11.glTranslated(0, -9F * scale, 0);
        GL11.glTranslated(0, 0, -4.0F * scale);
        
        double headRot = entity.prevRotationYawHead + (entity.rotationYawHead - entity.prevRotationYawHead) * ModClientFMLEventHandler.renderTickTime;
        GL11.glRotated(headRot - rot, 0, 1, 0);
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
            if (skin == null) {
                return;
            }
            GL11.glEnable(GL11.GL_NORMALIZE);
            float scale = 1F / 16F;
            for (int i = 0; i < skin.getParts().size(); i++) {
                SkinPartRenderer.INSTANCE.renderPart(skin.getParts().get(i), scale, skinPointer.getSkinDye(), null, false);
            }
            GL11.glDisable(GL11.GL_NORMALIZE);
        }
    }
}
