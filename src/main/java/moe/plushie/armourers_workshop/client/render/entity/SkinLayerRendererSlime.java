package moe.plushie.armourers_workshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.client.render.entity.ISkinnableEntityRenderer;
import moe.plushie.armourers_workshop.api.common.skin.IEntityEquipment;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.handler.ModClientFMLEventHandler;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkinLayerRendererSlime implements ISkinnableEntityRenderer<EntitySlime> {

    //@Override
    public void render(EntitySlime entity, RenderBiped renderer, double x, double y, double z, IEntityEquipment entityEquipment) {
        GL11.glPushMatrix();
        float scale = 0.0625F;
        
        GL11.glTranslated(x, y, z);
        GL11.glScalef(1, -1, -1);
        
        
        double rot = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * ModClientFMLEventHandler.renderTickTime;
        GL11.glRotated(rot, 0, 1, 0);
        
        if (entity.deathTime > 0) {
            float angle = ((float)entity.deathTime + ModClientFMLEventHandler.renderTickTime - 1.0F) / 20.0F * 1.6F;
            angle = MathHelper.sqrt(angle);
            if (angle > 1.0F) {
                angle = 1.0F;
            }
            GL11.glRotatef(angle * 90F, 0.0F, 0.0F, 1.0F);
        }
        
        float f1 = (float)entity.getSlimeSize();
        float f2 = (entity.prevSquishFactor + (entity.squishFactor - entity.prevSquishFactor) * ModClientFMLEventHandler.renderTickTime) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        GL11.glScalef(f3 * f1, 1.0F / f3 * f1, f3 * f1);
        
        GL11.glTranslated(0, -0.12F * scale, 0);
        
        float headScale = 1.001F;
        GL11.glScalef(headScale, headScale, headScale);
        renderEquipmentType(entity, renderer, SkinTypeRegistry.skinHead, entityEquipment);
        GL11.glPopMatrix();
    }
    
    private void renderEquipmentType(EntityLivingBase entity, RenderBiped renderer, ISkinType skinType, IEntityEquipment equipmentData) {
        if (equipmentData.haveEquipment(skinType, 0)) {
            ISkinDescriptor skinPointer = equipmentData.getSkinPointer(skinType, 0);
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
            if (skin == null) {
                return;
            }
            GL11.glEnable(GL11.GL_NORMALIZE);
            float scale = 1F / 16F;
            for (int i = 0; i < skin.getParts().size(); i++) {
                SkinPartRenderer.INSTANCE.renderPart(new SkinPartRenderData(skin.getParts().get(i), scale, skinPointer.getSkinDye(), null, 0, false, false, false, null));
            }
            GL11.glDisable(GL11.GL_NORMALIZE);
        }
    }
}
