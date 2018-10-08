package moe.plushie.armourers_workshop.client.render.entity;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHandSide;

public class SkinLayerRendererHeldItem extends LayerHeldItem {

    private final LayerRenderer<?> oldLayerRenderer;
    
    public SkinLayerRendererHeldItem(RenderLivingBase<?> livingEntityRendererIn, LayerRenderer<?> oldLayerRenderer) {
        super(livingEntityRendererIn);
        this.oldLayerRenderer = oldLayerRenderer;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean flag = entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT;
        ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
        ItemStack itemstack1 = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();
        
        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
            IEntitySkinCapability skinCapability = EntitySkinCapability.get(entitylivingbaseIn);
            GlStateManager.pushMatrix();
            
            if (this.livingEntityRenderer.getMainModel().isChild) {
                float f = 0.5F;
                GlStateManager.translate(0.0F, 0.75F, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
            }

            this.renderHeldItem(entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, skinCapability);
            this.renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, skinCapability);
            GlStateManager.popMatrix();
        }
    }

    private void renderHeldItem(EntityLivingBase entityLivingBase, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, EnumHandSide handSide, IEntitySkinCapability skinCapability) {
        if (!itemStack.isEmpty()) {
            GlStateManager.pushMatrix();

            if (entityLivingBase.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }
            // Forge: moved this call down, fixes incorrect offset while sneaking.
            this.translateToHand(handSide);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            boolean flag = handSide == EnumHandSide.LEFT;
            GlStateManager.translate((float) (flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            
            ISkinDescriptor descriptorSword = skinCapability.getSkinDescriptor(SkinTypeRegistry.skinSword, 0);
            ISkinDescriptor descriptorShield = skinCapability.getSkinDescriptor(SkinTypeRegistry.skinShield, 0);
            
            if (itemStack.getItem() instanceof ItemSword & descriptorSword != null) {
                GlStateManager.pushMatrix();
                GlStateManager.enableCull();
                GlStateManager.scale(-1, -1, 1);
                GlStateManager.translate(0, 0.0625F * 2, 0.0625F * 2);
                if (flag) {
                    GlStateManager.scale(-1, 1, 1);
                    GlStateManager.cullFace(CullFace.FRONT);
                }
                SkinItemRenderHelper.renderSkinWithoutHelper(descriptorSword, false);
                if (flag) {
                    GlStateManager.cullFace(CullFace.BACK);
                }
                GlStateManager.disableCull();
                GlStateManager.popMatrix();
            } else if (itemStack.getItem() instanceof ItemShield & descriptorShield != null) {
                GlStateManager.pushMatrix();
                GlStateManager.enableCull();
                GlStateManager.scale(-1, -1, 1);
                GlStateManager.translate(0, 0.0625F * 2, 0.0625F * 2);
                if (flag) {
                    GlStateManager.scale(-1, 1, 1);
                    GlStateManager.cullFace(CullFace.FRONT);
                }
                SkinItemRenderHelper.renderSkinWithoutHelper(descriptorShield, false);
                if (flag) {
                    GlStateManager.cullFace(CullFace.BACK);
                }
                GlStateManager.disableCull();
                GlStateManager.popMatrix();
            } else {
                Minecraft.getMinecraft().getItemRenderer().renderItemSide(entityLivingBase, itemStack, transformType, flag);
            }
            
            GlStateManager.popMatrix();
        }
    }
}
