package moe.plushie.armourers_workshop.core.render.entity;//package moe.plushie.armourers_workshop.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinLayerRendererHeldItem<T extends LivingEntity, M extends EntityModel<T> & IHasArm> extends HeldItemLayer<T, M> {

    public BakedSkin bakedSkin;

    public SkinLayerRendererHeldItem(IEntityRenderer<T, M> p_i50934_1_) {
        super(p_i50934_1_);
//        this.oldLayerRenderer = oldLayerRenderer;
    }

//    @Override
//    public void render(MatrixStack matrix, IRenderTypeBuffer renderer, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks,
//                       float ageInTicks, float netHeadYaw, float headPitch) {
////        super.render(matrix, renderer, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
//
////        boolean flag = p_225628_4_.getMainArm() == HandSide.RIGHT;
////        ItemStack itemstack = flag ? p_225628_4_.getOffhandItem() : p_225628_4_.getMainHandItem();
////        ItemStack itemstack1 = flag ? p_225628_4_.getMainHandItem() : p_225628_4_.getOffhandItem();
////        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
////            p_225628_1_.pushPose();
////            if (this.getParentModel().young) {
////                float f = 0.5F;
////                p_225628_1_.translate(0.0D, 0.75D, 0.0D);
////                p_225628_1_.scale(0.5F, 0.5F, 0.5F);
////            }
////
////            this.renderArmWithItem(p_225628_4_, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HandSide.RIGHT, p_225628_1_, p_225628_2_, p_225628_3_);
////            this.renderArmWithItem(p_225628_4_, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HandSide.LEFT, p_225628_1_, p_225628_2_, p_225628_3_);
////            p_225628_1_.popPose();
////        }
//matrix.pushPose();
//getParentModel().translateToHand(HandSide.LEFT, matrix);
//        matrix.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
//        matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
//        boolean flag = entity.getMainArm() == HandSide.LEFT;
//        matrix.translate((double)((float)(flag ? -1 : 1) / 16.0F), 0.125D, -0.625D);
//
//        ItemStack itemStack = entity.getMainHandItem();
//        if (itemStack != null && !itemStack.isEmpty()) {
//            Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, itemStack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, false, matrix, renderer, packedLightIn);
//        }
//
//        float f = 1f / 16f;
//        matrix.pushPose();
//        matrix.scale(f, f, f);
////        this.getParentModel().translateToHand(entity.getMainArm(), matrix);
////        matrix.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
////        matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
////        boolean flag = entity.getMainArm() == HandSide.LEFT;
////        matrix.translate((double)((float)(flag ? -1 : 1)), 0.125D, -0.625D);
//        SkinModelRenderer.INSTANCE.renderSkin(bakedSkin, getParentModel(), matrix, renderer);
//        matrix.popPose();
//        matrix.popPose();
//    }

    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderType, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
//        super.render(matrixStack, renderType, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);

        boolean flag = entity.getMainArm() == HandSide.RIGHT;
        ItemStack itemstack = flag ? entity.getOffhandItem() : entity.getMainHandItem();
        ItemStack itemstack1 = flag ? entity.getMainHandItem() : entity.getOffhandItem();
        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
            matrixStack.pushPose();
//            if (this.getParentModel().young) {
//                float f = 0.5F;
//                matrixStack.translate(0.0D, 0.75D, 0.0D);
//                matrixStack.scale(0.5F, 0.5F, 0.5F);
//            }

            this.renderArmWithItem(entity, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HandSide.RIGHT, matrixStack, renderType, packedLightIn);
            this.renderArmWithItem(entity, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HandSide.LEFT, matrixStack, renderType, packedLightIn);
            matrixStack.popPose();
        }
    }

    private void renderArmWithItem(LivingEntity p_229135_1_, ItemStack p_229135_2_, ItemCameraTransforms.TransformType p_229135_3_, HandSide p_229135_4_, MatrixStack matrix, IRenderTypeBuffer renderer, int p_229135_7_) {
        if (!p_229135_2_.isEmpty()) {
            matrix.pushPose();
            this.getParentModel().translateToHand(p_229135_4_, matrix);
            matrix.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            matrix.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            boolean flag = p_229135_4_ == HandSide.LEFT;
//            matrix.translate((double)((float)(flag ? -1 : 1) / 16.0F), 0.125D, -0.625D);
            // -1/1 2 -10

            float f = 1f / 16f;
            matrix.pushPose();
            matrix.scale(f, f, f);

                        matrix.translate(flag ? -1 : 1, 0, -8);

            SkinModelRenderer.INSTANCE.renderSkin(bakedSkin, getParentModel(), matrix, renderer);
            matrix.popPose();

            matrix.popPose();
        }
    }


//    @Override
//    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//        boolean flag = entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT;
//        ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
//        ItemStack itemstack1 = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();
//
//        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
//            IEntitySkinCapability skinCapability = EntitySkinCapability.get(entitylivingbaseIn);
//            GlStateManager.pushMatrix();
//
//            if (this.livingEntityRenderer.getMainModel().isChild) {
//                float f = 0.5F;
//                GlStateManager.translate(0.0F, 0.75F, 0.0F);
//                GlStateManager.scale(0.5F, 0.5F, 0.5F);
//            }
//
//            this.renderHeldItem(entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, skinCapability);
//            this.renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, skinCapability);
//            GlStateManager.popMatrix();
//        }
//    }
//
//    private void renderHeldItem(EntityLivingBase entityLivingBase, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, EnumHandSide handSide, IEntitySkinCapability skinCapability) {
//        if (!itemStack.isEmpty()) {
//            GlStateManager.pushMatrix();
//
//            if (entityLivingBase.isSneaking()) {
//                GlStateManager.translate(0.0F, 0.2F, 0.0F);
//            }
//            // Forge: moved this call down, fixes incorrect offset while sneaking.
//            this.translateToHand(handSide);
//            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
//            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
//            boolean flag = handSide == EnumHandSide.LEFT;
//            GlStateManager.translate((float) (flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
//
//            ISkinType[] skinTypes = new ISkinType[] {
//                    SkinTypeRegistry.skinSword,
//                    SkinTypeRegistry.skinShield,
//                    SkinTypeRegistry.skinBow,
//
//                    SkinTypeRegistry.skinPickaxe,
//                    SkinTypeRegistry.skinAxe,
//                    SkinTypeRegistry.skinShovel,
//                    SkinTypeRegistry.skinHoe,
//
//                    SkinTypeRegistry.skinItem
//            };
//
//            boolean slim = false;
//            if (entityLivingBase instanceof EntityPlayer) {
//                slim = SkinModelRenderHelper.isPlayersArmSlim((ModelBiped) livingEntityRenderer.getMainModel(), (EntityPlayer) entityLivingBase, handSide);
//            }
//
//            boolean didRender = false;
//            for (int i = 0; i < ItemOverrideType.values().length; i++) {
//                ItemOverrideType overrideType = ItemOverrideType.values()[i];
//                if (ModAddonManager.isOverrideItem(overrideType, itemStack.getItem())) {
//                    ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
//                    if (descriptor == null) {
//                        descriptor = skinCapability.getSkinDescriptor(skinTypes[i], 0);
//                    }
//                    if (descriptor != null) {
//                        GlStateManager.pushMatrix();
//                        GlStateManager.enableCull();
//                        GlStateManager.scale(-1, -1, 1);
//                        GlStateManager.translate(0, 0.0625F * 2, 0.0625F * 2);
//                        if (flag) {
//                            GlStateManager.scale(-1, 1, 1);
//                            GlStateManager.cullFace(CullFace.FRONT);
//                        }
//                        if (overrideType != ItemOverrideType.BOW) {
//                            // ((ModelBiped)this.livingEntityRenderer.getMainModel()).
//                            Skin skin = ClientSkinCache.INSTANCE.getSkin(descriptor);
//                            if (skin != null) {
//                                if (slim) {
//                                    GL11.glScaled(0.75F, 1F, 1F);
//                                }
//                                IEquipmentModel targetModel = SkinModelRenderHelper.INSTANCE.getTypeHelperForModel(ModelType.MODEL_BIPED, descriptor.getIdentifier().getType());
//                                targetModel.render(entityLivingBase, skin, (ModelBiped) livingEntityRenderer.getMainModel(), false, descriptor.getSkinDye(), null, true, 0, true);
//                                // SkinItemRenderHelper.renderSkinWithHelper(skin, descriptor, false, true);
//                            }
//
//                            // SkinItemRenderHelper.renderSkinWithoutHelper(descriptor, false);
//                        } else {
//                            Skin skin = ClientSkinCache.INSTANCE.getSkin(descriptor);
//                            if (skin != null) {
//                                int useCount = entityLivingBase.getItemInUseCount();
//                                ModelSkinBow model = SkinModelRenderHelper.INSTANCE.modelBow;
//                                model.frame = getAnimationFrame(entityLivingBase.getItemInUseMaxCount());
//                                // ModLogger.log("useCount:" + useCount + " maxUse:" +
//                                // entityLivingBase.getItemInUseMaxCount());
//
//                                model.render(entityLivingBase, skin, false, descriptor.getSkinDye(), null, false, 0, false);
//                            }
//                        }
//
//                        if (flag) {
//                            GlStateManager.cullFace(CullFace.BACK);
//                        }
//                        GlStateManager.disableCull();
//                        GlStateManager.popMatrix();
//                        didRender = true;
//                        break;
//                    }
//                }
//            }
//            if (!didRender) {
//                Minecraft.getMinecraft().getItemRenderer().renderItemSide(entityLivingBase, itemStack, transformType, flag);
//            }
//            GlStateManager.popMatrix();
//        }
//    }
//
//    private int getAnimationFrame(int useCount) {
//        if (useCount >= 18) {
//            return 2;
//        }
//        if (useCount > 13) {
//            return 1;
//        }
//        return 0;
//    }
}
