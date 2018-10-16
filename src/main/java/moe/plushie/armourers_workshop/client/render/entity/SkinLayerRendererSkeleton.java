package moe.plushie.armourers_workshop.client.render.entity;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkinLayerRendererSkeleton extends SkinLayerRenderer<EntitySkeleton, RenderSkeleton> {
    
    public SkinLayerRendererSkeleton(RenderSkeleton renderSkeleton) {
        super(renderSkeleton);
    }
    
    @Override
    protected void setRotTranForPartType(EntitySkeleton entitylivingbaseIn, ISkinType skinType, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    }
}
