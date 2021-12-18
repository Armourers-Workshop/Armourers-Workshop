package moe.plushie.armourers_workshop.core.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import moe.plushie.armourers_workshop.core.render.part.SkinPartRenderer;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import moe.plushie.armourers_workshop.core.utils.UtilColour;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;


@OnlyIn(Dist.CLIENT)
public final class SkinModelRenderer {

    public static SkinModelRenderer INSTANCE = new SkinModelRenderer();

//    public final ModelSkinHead modelHead = new ModelSkinHead();
//    public final ModelSkinChest modelChest = new ModelSkinChest();
//    public final ModelSkinLegs modelLegs = new ModelSkinLegs();
//    public final ModelSkinFeet modelFeet = new ModelSkinFeet();
//    public final ModelSkinWings modelWings = new ModelSkinWings();
//    public final ModelSkinOutfit modelOutfit = new ModelSkinOutfit();

//    public final ModelSkinItem modelItem = new ModelSkinItem();
//    public final ModelSkinBow modelBow = new ModelSkinBow();

//    public final ModelDummy modelHelperDummy = new ModelDummy();

    private SkinModelRenderer() {
    }

    private boolean isPlayerWearingSkirt(PlayerEntity player) {
//        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
//        if (skinCapability == null) {
//            return false;
//        }
        boolean limitLimbs = false;
//        for (int i = 0; i < skinCapability.getSlotCountForSkinType(SkinTypes.BIPED_LEGS); i++) {
//            ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypes.BIPED_LEGS, i);
//            if (skinDescriptor != null) {
//                Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
//                if (skin != null) {
//                    if (SkinProperty.MODEL_LEGS_LIMIT_LIMBS.getValue(skin.getProperties())) {
//                        limitLimbs = true;
//                        break;
//                    }
//                }
//            }
//        }
//        if (!limitLimbs) {
//            for (int i = 0; i < skinCapability.getSlotCountForSkinType(SkinTypes.BIPED_OUTFIT); i++) {
//                ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypes.BIPED_OUTFIT, i);
//                if (skinDescriptor != null) {
//                    Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
//                    if (skin != null) {
//                        if (SkinProperty.MODEL_LEGS_LIMIT_LIMBS.getValue(skin.getProperties())) {
//                            limitLimbs = true;
//                            break;
//                        }
//                    }
//                }
//            }
//        }

        return limitLimbs;
    }

//    public static boolean isPlayersArmSlim(BipedModel modelBiped, PlayerEntity entityPlayer, EnumHandSide handSide) {
//        boolean slim = false;
//        SkinProperty<Boolean> targetProp = null;
//        if (handSide == EnumHandSide.LEFT) {
//            slim = modelBiped.bipedLeftArm.rotationPointY == 2.5F;
//            targetProp = SkinProperty.MODEL_OVERRIDE_ARM_LEFT;
//        } else {
//            slim = modelBiped.bipedRightArm.rotationPointY == 2.5F;
//            targetProp = SkinProperty.MODEL_OVERRIDE_ARM_RIGHT;
//        }
//
//        boolean armHidden = false;
//        IEntitySkinCapability skinCapability = EntitySkinCapability.get(entityPlayer);
//        if (skinCapability == null) {
//            return armHidden;
//        }
//        for (int i = 0; i < skinCapability.getSlotCountForSkinType(SkinTypes.BIPED_CHEST); i++) {
//            ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypes.BIPED_CHEST, i);
//            if (skinDescriptor != null) {
//                Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
//                if (skin != null) {
//                    if (targetProp.getValue(skin.getProperties())) {
//                        armHidden = true;
//                        break;
//                    }
//                }
//            }
//        }
//        if (!armHidden) {
//            for (int i = 0; i < skinCapability.getSlotCountForSkinType(SkinTypes.BIPED_OUTFIT); i++) {
//                ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypes.BIPED_OUTFIT, i);
//                if (skinDescriptor != null) {
//                    Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
//                    if (skin != null) {
//                        if (targetProp.getValue(skin.getProperties())) {
//                            armHidden = true;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        if (armHidden) {
//            return false;
//        }
//        return slim;
//    }
//
//    @SubscribeEvent
//    public void onRender(RenderPlayerEvent.Pre event) {
//        EntityPlayer player = event.getEntityPlayer();
//        // Limit the players limbs if they have a skirt equipped.
//        // A proper lady should not swing her legs around!
//        if (isPlayerWearingSkirt(player)) {
//            if (player.limbSwingAmount > 0.25F) {
//                player.limbSwingAmount = 0.25F;
//                player.prevLimbSwingAmount = 0.25F;
//            }
//        }
//    }
//
//    public ModelTypeHelper getTypeHelperForModel(ModelType modelType, ISkinType skinType) {
//        ModelTypeHelper typeHelper = helperModelsMap.get(skinType.getRegistryName() + ":" + modelType.name());
//        if (typeHelper == null) {
////            return modelHelperDummy;
//        }
//        return typeHelper;
//    }
//
//    public void registerSkinTypeHelperForModel(ModelType modelType, ISkinType skinType, ModelTypeHelper typeHelper) {
//        helperModelsMap.put(skinType.getRegistryName() + ":" + modelType.name(), typeHelper);
//    }
//
//    public ModelTypeHelper agetTypeHelperForModel(ModelType modelType, ISkinType skinType) {
//        return helperModelsMap.get(skinType.getRegistryName() + ":" + modelType.name());
//    }

    public void renderSkin(Entity entity, BakedSkin bakedSkin, Model model, ItemCameraTransforms.TransformType transformType, int light, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
        int i = 0;
        Skin skin = bakedSkin.getSkin();
        SkinDye skinDye = bakedSkin.getSkinDye();

        for (SkinPart skinPart : skin.getParts()) {
            //
            ModelRenderer modelRenderer = ModelTransformer.getModelRenderer(skinPart, model, transformType);
            if (modelRenderer == null) {
                continue;
            }
            matrixStack.pushPose();
            ModelTransformer.apply(matrixStack, modelRenderer);
            SkinUtils.apply(matrixStack, entity, skinPart);

            // render the contents.
            SkinPartRenderer.renderPart(skinPart, skinDye, light, partialTicks, matrixStack, renderer);

            // render the debug skin part bounding box.
            if (SkinConfig.showDebugPartBounds) {
                RenderUtils.drawBoundingBox(matrixStack, skinPart.getRenderShape(), UtilColour.getPaletteColor(i++));
            }

            matrixStack.popPose();
        }

        // render the debug skin bounding box.
        if (SkinConfig.showDebugFullBounds) {
            RenderUtils.drawBoundingBox(matrixStack, skin.getRenderShape(model, transformType), Color.YELLOW);
        }
    }


//    public boolean renderEquipmentPart(Skin skin, SkinRenderData renderData, Entity entity, ModelBiped modelBiped) {
//        if (skin == null) {
//            return false;
//        }
//        IEquipmentModel model = getTypeHelperForModel(ModelType.MODEL_BIPED, skin.getType());
//        GlStateManager.pushAttrib();
//        GlStateManager.enableCull();
//        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
//        GlStateManager.enableBlend();
//        GlStateManager.enableRescaleNormal();
//        model.render(entity, skin, modelBiped, renderData);
//        GlStateManager.disableRescaleNormal();
//        GlStateManager.disableBlend();
//        GlStateManager.disableCull();
//        GlStateManager.popAttrib();
//        return true;
//    }
//
//    public boolean renderEquipmentPart(Entity entity, ModelBiped modelBiped, Skin skin, ISkinDye skinDye, IExtraColours extraColours, double distance, boolean doLodLoading) {
//        if (skin == null) {
//            return false;
//        }
//        IEquipmentModel model = getTypeHelperForModel(ModelType.MODEL_BIPED, skin.getType());
//        GlStateManager.pushAttrib();
//        GlStateManager.enableCull();
//        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
//        GlStateManager.enableBlend();
//        GlStateManager.enableRescaleNormal();
//        model.render(entity, skin, modelBiped, false, skinDye, extraColours, false, distance, doLodLoading);
//        GlStateManager.disableRescaleNormal();
//        GlStateManager.disableBlend();
//        GlStateManager.disableCull();
//        GlStateManager.popAttrib();
//        return true;
//    }

}
