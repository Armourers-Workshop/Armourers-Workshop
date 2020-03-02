package moe.plushie.armourers_workshop.client.render;

import java.util.HashMap;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.model.skin.IEquipmentModel;
import moe.plushie.armourers_workshop.client.model.skin.ModelDummy;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinBow;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinChest;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinFeet;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinHead;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinItem;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinLegs;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinOutfit;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinWings;
import moe.plushie.armourers_workshop.client.model.skin.ModelTypeHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperty;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Helps render skins on the player and other entities.
 *
 * TODO Clean up this class it's a mess >:|
 *
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public final class SkinModelRenderHelper {

    public static SkinModelRenderHelper INSTANCE;

    public static void init() {
        INSTANCE = new SkinModelRenderHelper();
    }

    private final HashMap<String, ModelTypeHelper> helperModelsMap;

    public final ModelSkinHead modelHead = new ModelSkinHead();
    public final ModelSkinChest modelChest = new ModelSkinChest();
    public final ModelSkinLegs modelLegs = new ModelSkinLegs();
    public final ModelSkinFeet modelFeet = new ModelSkinFeet();
    public final ModelSkinWings modelWings = new ModelSkinWings();
    public final ModelSkinOutfit modelOutfit = new ModelSkinOutfit();

    public final ModelSkinItem modelItem = new ModelSkinItem();
    public final ModelSkinBow modelBow = new ModelSkinBow();

    public final ModelDummy modelHelperDummy = new ModelDummy();

    public EntityPlayer targetPlayer = null;

    private SkinModelRenderHelper() {
        MinecraftForge.EVENT_BUS.register(this);
        helperModelsMap = new HashMap<String, ModelTypeHelper>();

        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinHead, modelHead);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinChest, modelChest);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinLegs, modelLegs);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinFeet, modelFeet);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinWings, modelWings);

        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinSword, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinShield, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinBow, modelBow);

        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinPickaxe, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinAxe, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinShovel, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinHoe, modelItem);
        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinItem, modelItem);

        registerSkinTypeHelperForModel(ModelType.MODEL_BIPED, SkinTypeRegistry.skinOutfit, modelOutfit);
    }

    private boolean isPlayerWearingSkirt(EntityPlayer player) {
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        if (skinCapability == null) {
            return false;
        }
        boolean limitLimbs = false;
        for (int i = 0; i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinLegs); i++) {
            ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypeRegistry.skinLegs, i);
            if (skinDescriptor != null) {
                Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
                if (skin != null) {
                    if (SkinProperties.PROP_MODEL_LEGS_LIMIT_LIMBS.getValue(skin.getProperties())) {
                        limitLimbs = true;
                        break;
                    }
                }
            }
        }
        if (!limitLimbs) {
            for (int i = 0; i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinOutfit); i++) {
                ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypeRegistry.skinOutfit, i);
                if (skinDescriptor != null) {
                    Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
                    if (skin != null) {
                        if (SkinProperties.PROP_MODEL_LEGS_LIMIT_LIMBS.getValue(skin.getProperties())) {
                            limitLimbs = true;
                            break;
                        }
                    }
                }
            }
        }

        return limitLimbs;
    }
    
    public static boolean isPlayersArmSlim(ModelBiped modelBiped, EntityPlayer entityPlayer, EnumHandSide handSide) {
        boolean slim = false;
        SkinProperty<Boolean> targetProp = null;
        if (handSide == EnumHandSide.LEFT) {
            slim = modelBiped.bipedLeftArm.rotationPointY == 2.5F;
            targetProp = SkinProperties.PROP_MODEL_OVERRIDE_ARM_LEFT;
        } else {
            slim = modelBiped.bipedRightArm.rotationPointY == 2.5F;
            targetProp = SkinProperties.PROP_MODEL_OVERRIDE_ARM_RIGHT;
        }
        
        boolean armHidden = false;
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(entityPlayer);
        if (skinCapability == null) {
            return armHidden;
        }
        for (int i = 0; i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinChest); i++) {
            ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypeRegistry.skinChest, i);
            if (skinDescriptor != null) {
                Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
                if (skin != null) {
                    if (targetProp.getValue(skin.getProperties())) {
                        armHidden = true;
                        break;
                    }
                }
            }
        }
        if (!armHidden) {
            for (int i = 0; i < skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinOutfit); i++) {
                ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(SkinTypeRegistry.skinOutfit, i);
                if (skinDescriptor != null) {
                    Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
                    if (skin != null) {
                        if (targetProp.getValue(skin.getProperties())) {
                            armHidden = true;
                            break;
                        }
                    }
                }
            }
        }
        if (armHidden) {
            return false;
        }
        return slim;
    }

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        // Limit the players limbs if they have a skirt equipped.
        // A proper lady should not swing her legs around!
        if (isPlayerWearingSkirt(player)) {
            if (player.limbSwingAmount > 0.25F) {
                player.limbSwingAmount = 0.25F;
                player.prevLimbSwingAmount = 0.25F;
            }
        }
    }

    public ModelTypeHelper getTypeHelperForModel(ModelType modelType, ISkinType skinType) {
        ModelTypeHelper typeHelper = helperModelsMap.get(skinType.getRegistryName() + ":" + modelType.name());
        if (typeHelper == null) {
            return modelHelperDummy;
        }
        return typeHelper;
    }

    public void registerSkinTypeHelperForModel(ModelType modelType, ISkinType skinType, ModelTypeHelper typeHelper) {
        helperModelsMap.put(skinType.getRegistryName() + ":" + modelType.name(), typeHelper);
    }

    public ModelTypeHelper agetTypeHelperForModel(ModelType modelType, ISkinType skinType) {
        return helperModelsMap.get(skinType.getRegistryName() + ":" + modelType.name());
    }
    
    public boolean renderEquipmentPart(Skin skin, SkinRenderData renderData, Entity entity, ModelBiped modelBiped) {
        if (skin == null) {
            return false;
        }
        IEquipmentModel model = getTypeHelperForModel(ModelType.MODEL_BIPED, skin.getSkinType());
        GlStateManager.pushAttrib();
        GlStateManager.enableCull();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.enableRescaleNormal();
        model.render(entity, skin, modelBiped, renderData);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.disableCull();
        GlStateManager.popAttrib();
        return true;
    }
    
    public boolean renderEquipmentPart(Entity entity, ModelBiped modelBiped, Skin skin, ISkinDye skinDye, IExtraColours extraColours, double distance, boolean doLodLoading) {
        if (skin == null) {
            return false;
        }
        IEquipmentModel model = getTypeHelperForModel(ModelType.MODEL_BIPED, skin.getSkinType());
        GlStateManager.pushAttrib();
        GlStateManager.enableCull();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.enableRescaleNormal();
        model.render(entity, skin, modelBiped, false, skinDye, extraColours, false, distance, doLodLoading);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.disableCull();
        GlStateManager.popAttrib();
        return true;
    }

    public static enum ModelType {
        MODEL_BIPED,
    }
}
