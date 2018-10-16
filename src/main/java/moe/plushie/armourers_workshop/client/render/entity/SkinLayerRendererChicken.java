package moe.plushie.armourers_workshop.client.render.entity;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkinLayerRendererChicken extends SkinLayerRenderer<EntityChicken, RenderChicken> {
    
    public SkinLayerRendererChicken(RenderChicken renderChicken) {
        super(renderChicken);
    }
    
    @Override
    protected void setRotTranForPartType(EntityChicken entityLivingBase, ISkinType skinType, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (skinType == SkinTypeRegistry.skinHead) {
            GlStateManager.translate(0, 15F * scale, 0);
            GlStateManager.translate(0, 0, -4F * scale);
            
            GlStateManager.rotate(netHeadYaw, 0, 1, 0);
            GlStateManager.rotate(headPitch, 1, 0, 0);
            float headScale = 0.5F;
            GlStateManager.scale(headScale, headScale * 1.5F, headScale);
        }
    }
}
