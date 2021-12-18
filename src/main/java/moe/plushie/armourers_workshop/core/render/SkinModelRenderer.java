package moe.plushie.armourers_workshop.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinType;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.model.skin.*;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
import moe.plushie.armourers_workshop.core.skin.type.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.HashMap;

/**
 * Helps render skins on the player and other entities.
 *
 * TODO Clean up this class it's a mess >:|
 *
 * @author RiskyKen
 *
 */
@OnlyIn(Dist.CLIENT)
public final class SkinModelRenderer {

    public static SkinModelRenderer INSTANCE;

    public static void init() {
        INSTANCE = new SkinModelRenderer();
    }

    private final HashMap<String, ModelTypeHelper> helperModelsMap;

//    public final ModelSkinHead modelHead = new ModelSkinHead();
//    public final ModelSkinChest modelChest = new ModelSkinChest();
//    public final ModelSkinLegs modelLegs = new ModelSkinLegs();
//    public final ModelSkinFeet modelFeet = new ModelSkinFeet();
//    public final ModelSkinWings modelWings = new ModelSkinWings();
    public final ModelSkinOutfit modelOutfit = new ModelSkinOutfit();

    public final ModelSkinItem modelItem = new ModelSkinItem();
//    public final ModelSkinBow modelBow = new ModelSkinBow();

//    public final ModelDummy modelHelperDummy = new ModelDummy();

    public PlayerEntity targetPlayer = null;

    private SkinModelRenderer() {
//        MinecraftForge.EVENT_BUS.register(this);
        helperModelsMap = new HashMap<String, ModelTypeHelper>();

//        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.BIPED_HEAD, modelHead);
//        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.BIPED_CHEST, modelChest);
//        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.BIPED_LEGS, modelLegs);
//        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.BIPED_FEET, modelFeet);
//        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.BIPED_WINGS, modelWings);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.BIPED_HEAD, modelOutfit);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.BIPED_CHEST, modelOutfit);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.BIPED_LEGS, modelOutfit);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.BIPED_FEET, modelOutfit);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.BIPED_WINGS, modelOutfit);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.BIPED_OUTFIT, modelOutfit);

        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.ITEM_SWORD, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.ITEM_SHIELD, modelItem);
//        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.ITEM_BOW, modelBow);

        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.TOOL_PICKAXE, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.TOOL_AXE, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.TOOL_SHOVEL, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.TOOL_HOPE, modelItem);

        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypes.ITEM, modelItem);

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

    public ModelTypeHelper getTypeHelperForModel(ModelType modelType, ISkinType skinType) {
        ModelTypeHelper typeHelper = helperModelsMap.get(skinType.getRegistryName() + ":" + modelType.name());
        if (typeHelper == null) {
//            return modelHelperDummy;
        }
        return typeHelper;
    }

    public void registerSkinTypeHelperForModel(ModelType modelType, ISkinType skinType, ModelTypeHelper typeHelper) {
        helperModelsMap.put(skinType.getRegistryName() + ":" + modelType.name(), typeHelper);
    }

    public ModelTypeHelper agetTypeHelperForModel(ModelType modelType, ISkinType skinType) {
        return helperModelsMap.get(skinType.getRegistryName() + ":" + modelType.name());
    }


    public void renderSkin(BakedSkin bakedSkin, Model model, MatrixStack matrixStack, IRenderTypeBuffer renderer) {
        matrixStack.pushPose();

        Skin skin = bakedSkin.getSkin();
        SkinDye skinDye = bakedSkin.getSkinDye();

        if (SkinConfig.showDebugSpin) {
            float angle = (((System.currentTimeMillis() / 10) % 360));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(angle));
        }

        // draw
        IEquipmentModel targetModel = getTypeHelperForModel(SkinModelRenderer.ModelType.MODEL_BIPED, skin.getType());
        targetModel.render(null, bakedSkin, model, true, 0, false, matrixStack, renderer);
//        targetModel.render(null, skin, bipedModel, false, skinDye, null, true, 0, false, matrixStack, );

//        if (SkinConfig.showDebugFullBounds) {
//            RenderUtils.drawBoundingBox(matrixStack, skin.getRenderShape(model), Color.YELLOW);
//        }

        matrixStack.popPose();
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

    public static enum ModelType {
        MODEL_BIPED,
    }
}
