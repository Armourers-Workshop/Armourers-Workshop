package moe.plushie.armourers_workshop.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.base.AWEntities;
import moe.plushie.armourers_workshop.core.color.ColorScheme;
import moe.plushie.armourers_workshop.core.color.PaintColor;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderBuffer;
import moe.plushie.armourers_workshop.core.render.buffer.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.render.model.ModelTransformer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRendererManager;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.data.adapter.SkinAdapter;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;


@OnlyIn(Dist.CLIENT)
public final class SkinModelRenderer {



//    public static void renderSkin(BakedSkin bakedSkin, ColorScheme scheme, Entity entity, Model model, ItemCameraTransforms.TransformType transformType, int light, float partialTicks, MatrixStack matrixStack, SkinRenderBuffer buffers) {
//        Skin skin = bakedSkin.getSkin();
//        SkinVertexBufferBuilder builder = buffers.getBuffer(skin);
//        SkinRenderer<Entity, Model> renderer = SkinRendererManager.getInstance().getRenderer(entity);
//        if (renderer == null) {
//            return;
//        }
//
//        int index = 0;
//        ColorScheme scheme1 = bakedSkin.resolve(entity, scheme);
//        for (BakedSkinPart bakedPart : bakedSkin.getSkinParts()) {
//            if (!renderer.prepare(entity, model, bakedSkin, bakedPart, transformType)) {
//                continue;
//            }
//            boolean shouldRenderPart = bakedSkin.shouldRenderPart(bakedPart, entity, transformType);
//            matrixStack.pushPose();
//            renderer.apply(entity, model, bakedSkin, bakedPart, transformType, partialTicks, matrixStack);
//            builder.addPartData(bakedPart, scheme1, light, partialTicks, matrixStack, shouldRenderPart);
//            if (shouldRenderPart) {
//                if (AWConfig.showDebugTargetPosition) {
//                    builder.addShapeData(AWConstants.ZERO, matrixStack);
//                }
//                if (AWConfig.showDebugPartFrame) {
//                    builder.addShapeData(bakedPart.getRenderShape().bounds(), ColorUtils.getPaletteColor(index++), matrixStack);
//                }
//            }
//            matrixStack.popPose();
//        }
//
//        if (AWConfig.showDebugFullFrame) {
//            builder.addShapeData(bakedSkin.getRenderShape(entity, model, transformType).bounds(), Color.RED, matrixStack);
//        }
//    }

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
