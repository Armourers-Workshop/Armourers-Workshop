package moe.plushie.armourers_workshop.core.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.render.SkinRenderBuffer;
import moe.plushie.armourers_workshop.core.render.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.render.model.ModelTransformer;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import moe.plushie.armourers_workshop.core.render.other.BakedSkinDye;
import moe.plushie.armourers_workshop.core.render.other.BakedSkinPart;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import moe.plushie.armourers_workshop.core.utils.UtilColour;
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

    private final static byte[][][] FACE_VERTEXES = new byte[][][]{
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},  // -y
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}}, // +y
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},  // -z
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}}, // +z
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},  // -x
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}}, // +x
    };

//    public final ModelSkinHead modelHead = new ModelSkinHead();
//    public final ModelSkinChest modelChest = new ModelSkinChest();
//    public final ModelSkinLegs modelLegs = new ModelSkinLegs();
//    public final ModelSkinFeet modelFeet = new ModelSkinFeet();
//    public final ModelSkinWings modelWings = new ModelSkinWings();
//    public final ModelSkinOutfit modelOutfit = new ModelSkinOutfit();

//    public final ModelSkinItem modelItem = new ModelSkinItem();
//    public final ModelSkinBow modelBow = new ModelSkinBow();

//    public final ModelDummy modelHelperDummy = new ModelDummy();
//        VillagerEntity
//        VillagerModel


    public static void renderSkin(BakedSkin bakedSkin, SkinDye dye, Entity entity, Model model, ItemCameraTransforms.TransformType transformType, int light, int partialTicks, MatrixStack matrixStack, SkinRenderBuffer buffers) {
        Skin skin = bakedSkin.getSkin();
        SkinVertexBufferBuilder builder = buffers.getBuffer(skin);

        int idx = 0;
        BakedSkinDye bakedDye = new BakedSkinDye(entity, bakedSkin.getSkinDye(), dye);
        for (BakedSkinPart bakedPart : bakedSkin.getSkinParts()) {
            ModelRenderer modelRenderer = ModelTransformer.getTransform(bakedPart.getType(), model, transformType);
            if (modelRenderer == null) {
                continue;
            }
            matrixStack.pushPose();
            ModelTransformer.apply(matrixStack, modelRenderer);
            SkinUtils.apply(matrixStack, entity, bakedPart.getPart(), partialTicks);

            builder.addPartData(bakedPart, bakedDye, light, partialTicks, matrixStack);
            if (SkinConfig.showDebugPartBounds) {
                builder.addShapeData(bakedPart.getRenderShape().bounds(), UtilColour.getPaletteColor(idx++), matrixStack);
            }
            matrixStack.popPose();
        }

        if (SkinConfig.showDebugFullBounds) {
            builder.addShapeData(bakedSkin.getRenderShape(model, transformType).bounds(), Color.RED, matrixStack);
        }
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

    public static void renderFace(IVertexBuilder builder, float x, float y, float z, int rgb, byte a, Direction dir, float u, float v) {
        byte[][] vertexes = FACE_VERTEXES[dir.get3DDataValue()];
        for (int i = 0; i < 4; ++i) {
            builder.vertex(x + vertexes[i][0], y + vertexes[i][1], z + vertexes[i][2])
                    .color(rgb >> 16 & 0xff, rgb >> 8 & 0xff, rgb & 0xff, a)
                    .uv(u / 256.0f, v / 256.0f)
                    .normal(vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
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
