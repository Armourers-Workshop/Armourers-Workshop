package moe.plushie.armourers_workshop.client.render.entity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.model.skin.IEquipmentModel;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinBow;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper.ModelType;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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

            ISkinType[] skinTypes = new ISkinType[] {
                    SkinTypeRegistry.skinSword,
                    SkinTypeRegistry.skinShield,
                    SkinTypeRegistry.skinBow,
                    
                    SkinTypeRegistry.skinPickaxe,
                    SkinTypeRegistry.skinAxe,
                    SkinTypeRegistry.skinShovel,
                    SkinTypeRegistry.skinHoe,
                    
                    SkinTypeRegistry.skinItem
            };

            boolean slim = false;
            if (entityLivingBase instanceof EntityPlayer) {
                slim = SkinModelRenderHelper.isPlayersArmSlim((ModelBiped) livingEntityRenderer.getMainModel(), (EntityPlayer) entityLivingBase, handSide);
            }

            boolean didRender = false;
            for (int i = 0; i < ItemOverrideType.values().length; i++) {
                ItemOverrideType overrideType = ItemOverrideType.values()[i];
                if (ModAddonManager.isOverrideItem(overrideType, itemStack.getItem())) {
                    ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
                    if (descriptor == null) {
                        descriptor = skinCapability.getSkinDescriptor(skinTypes[i], 0);
                    }
                    if (descriptor != null) {
                        GlStateManager.pushMatrix();
                        GlStateManager.enableCull();
                        GlStateManager.scale(-1, -1, 1);
                        GlStateManager.translate(0, 0.0625F * 2, 0.0625F * 2);
                        if (flag) {
                            GlStateManager.scale(-1, 1, 1);
                            GlStateManager.cullFace(CullFace.FRONT);
                        }
                        if (overrideType != ItemOverrideType.BOW) {
                            // ((ModelBiped)this.livingEntityRenderer.getMainModel()).
                            Skin skin = ClientSkinCache.INSTANCE.getSkin(descriptor);
                            if (skin != null) {
                                if (slim) {
                                    GL11.glScaled(0.75F, 1F, 1F);
                                }
                                IEquipmentModel targetModel = SkinModelRenderHelper.INSTANCE.getTypeHelperForModel(ModelType.MODEL_BIPED, descriptor.getIdentifier().getSkinType());
                                targetModel.render(entityLivingBase, skin, (ModelBiped) livingEntityRenderer.getMainModel(), false, descriptor.getSkinDye(), null, true, 0, true);
                                // SkinItemRenderHelper.renderSkinWithHelper(skin, descriptor, false, true);
                            }

                            // SkinItemRenderHelper.renderSkinWithoutHelper(descriptor, false);
                        } else {
                            Skin skin = ClientSkinCache.INSTANCE.getSkin(descriptor);
                            if (skin != null) {
                                int useCount = entityLivingBase.getItemInUseCount();
                                ModelSkinBow model = SkinModelRenderHelper.INSTANCE.modelBow;
                                model.frame = getAnimationFrame(entityLivingBase.getItemInUseMaxCount());
                                // ModLogger.log("useCount:" + useCount + " maxUse:" +
                                // entityLivingBase.getItemInUseMaxCount());

                                model.render(entityLivingBase, skin, false, descriptor.getSkinDye(), null, false, 0, false);
                            }
                        }

                        if (flag) {
                            GlStateManager.cullFace(CullFace.BACK);
                        }
                        GlStateManager.disableCull();
                        GlStateManager.popMatrix();
                        didRender = true;
                        break;
                    }
                }
            }
            if (!didRender) {
                Minecraft.getMinecraft().getItemRenderer().renderItemSide(entityLivingBase, itemStack, transformType, flag);
            }
            GlStateManager.popMatrix();
        }
    }

    private int getAnimationFrame(int useCount) {
        if (useCount >= 18) {
            return 2;
        }
        if (useCount > 13) {
            return 1;
        }
        return 0;
    }
}
