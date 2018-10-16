package moe.plushie.armourers_workshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.IEntityEquipment;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.handler.ModClientFMLEventHandler;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderGhast;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkinLayerRendererGhast extends SkinLayerRenderer<EntityGhast, RenderGhast> {

    public SkinLayerRendererGhast(RenderGhast renderGhast) {
        super(renderGhast);
    }
    
    public void render(EntityGhast entity, RenderEntity renderer, double x, double y, double z, IEntityEquipment entityEquipment) {
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
        
        GL11.glTranslated(0, 6.65F * scale, 0);
        
        double headRot = entity.prevRotationYawHead + (entity.rotationYawHead - entity.prevRotationYawHead) * ModClientFMLEventHandler.renderTickTime;
        GL11.glRotatef(entity.rotationPitch, 1, 0, 0);
        
        float headScale = 9.01F;
        GL11.glScalef(headScale, headScale, headScale);
        //renderEquipmentType(entity, renderer, SkinTypeRegistry.skinHead, entityEquipment);
        GL11.glPopMatrix();
    }
    
    @Override
    protected void setRotTranForPartType(EntityGhast entitylivingbaseIn, ISkinType skinType, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GL11.glTranslated(0, 25.65 * scale, 0);
        float headScale = 2.0001F;
        GL11.glScalef(headScale, headScale, headScale);
    }
}
