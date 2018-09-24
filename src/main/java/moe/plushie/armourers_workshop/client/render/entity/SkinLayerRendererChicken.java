package moe.plushie.armourers_workshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkinLayerRendererChicken implements LayerRenderer<EntityChicken> {
    
    @Override
    public void doRenderLayer(EntityChicken entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(entitylivingbaseIn);
        if (skinCapability == null) {
            return;
        }
        ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypeRegistry.skinHead, 0);
        if (skinDescriptor == null) {
            return;
        }
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor);
        if (skin == null) {
            return;
        }
        GlStateManager.pushMatrix();
        
        GlStateManager.translate(0, 15F * scale, 0);
        GlStateManager.translate(0, 0, -4F * scale);
        
        GlStateManager.rotate(netHeadYaw, 0, 1, 0);
        GlStateManager.rotate(headPitch, 1, 0, 0);
        
        float headScale = 0.5F;
        GL11.glScalef(headScale, headScale * 1.5F, headScale);
        for (int i = 0; i < skin.getParts().size(); i++) {
            SkinPartRenderer.INSTANCE.renderPart(skin.getParts().get(i), scale, skinDescriptor.getSkinDye(), null, false);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
