package moe.plushie.armourers_workshop.client.render.entity;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

public class SkinLayerRendererDummy extends SkinLayerRenderer<EntityLivingBase, RenderLivingBase> {

    public SkinLayerRendererDummy(RenderLivingBase<EntityLivingBase> renderLivingBase) {
        super(renderLivingBase);
    }
    
    @Override
    protected void setRotTranForPartType(EntityLivingBase entitylivingbaseIn, ISkinType skinType, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    }
}
