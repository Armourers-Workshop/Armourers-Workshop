package moe.plushie.armourers_workshop.client.render.entity;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinRenderData;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.proxies.ClientProxy.TexturePaintType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityMannequin<EntityMannequin> extends RenderEntity {

    private final ModelPlayer modelPlayerSmall = new ModelPlayer(0F, false);
    private final ModelPlayer modelPlayerNormal = new ModelPlayer(0F, false);

    public RenderEntityMannequin(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        
        GlStateManager.pushMatrix();

        float scale = 0.0625F;

        GlStateManager.translate(x, y, z);
        GlStateManager.scale(15F * scale, -15F * scale, -15F * scale);
        GlStateManager.translate(0, -24F * scale, 0);
        // GlStateManager.scale(1F, 0.7F, 1F);

        // super.doRender(entity, x, y, z, entityYaw, partialTicks);

        EntitySkinCapability skinCap = (EntitySkinCapability) EntitySkinCapability.get(entity);
        if (skinCap == null) {
            return;
        }

        skinCap.hideHead = false;
        skinCap.hideChest = false;
        skinCap.hideArmLeft = false;
        skinCap.hideArmRight = false;
        skinCap.hideLegLeft = false;
        skinCap.hideLegRight = false;

        skinCap.hideHeadOverlay = false;
        skinCap.hideChestOverlay = false;
        skinCap.hideArmLeftOverlay = false;
        skinCap.hideArmRightOverlay = false;
        skinCap.hideLegLeftOverlay = false;
        skinCap.hideLegRightOverlay = false;
        
        modelPlayerNormal.bipedHead.isHidden = false;
        modelPlayerNormal.bipedHeadwear.isHidden = false;
        modelPlayerNormal.bipedBody.isHidden = false;
        modelPlayerNormal.bipedBodyWear.isHidden = false;
        modelPlayerNormal.bipedLeftArm.isHidden = false;
        modelPlayerNormal.bipedLeftArmwear.isHidden = false;
        modelPlayerNormal.bipedRightArm.isHidden = false;
        modelPlayerNormal.bipedRightArmwear.isHidden = false;
        modelPlayerNormal.bipedLeftLeg.isHidden = false;
        modelPlayerNormal.bipedLeftLegwear.isHidden = false;
        modelPlayerNormal.bipedRightLeg.isHidden = false;
        modelPlayerNormal.bipedRightLegwear.isHidden = false;

        ISkinType[] skinTypes = skinCap.getValidSkinTypes();
        SkinModelRenderHelper modelRenderer = SkinModelRenderHelper.INSTANCE;
        IExtraColours extraColours = ExtraColours.EMPTY_COLOUR;
        IWardrobeCap wardrobe = WardrobeCap.get(entity);
        if (wardrobe != null) {
            extraColours = wardrobe.getExtraColours();
        }

        for (int i = 0; i < skinTypes.length; i++) {
            ISkinType skinType = skinTypes[i];
            if (skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypeRegistry.skinWings | skinType == SkinTypeRegistry.skinOutfit) {
                for (int skinIndex = 0; skinIndex < skinCap.getSlotCountForSkinType(skinType); skinIndex++) {
                    ISkinDescriptor skinDescriptor = skinCap.getSkinDescriptor(skinType, skinIndex);
                    if (skinDescriptor != null) {
                        renderSkin(entity, skinDescriptor, skinCap, wardrobe, extraColours, 0, entity != Minecraft.getMinecraft().player);
                    }
                }
            }
        }
        
        // Head
        if (skinCap.hideHead) {
            modelPlayerNormal.bipedHead.isHidden = true;
        }
        if (skinCap.hideHead | skinCap.hideHeadOverlay) {
            modelPlayerNormal.bipedHeadwear.isHidden = true;
        }

        // Chest
        if (skinCap.hideChest) {
            modelPlayerNormal.bipedBody.isHidden = true;

        }
        if (skinCap.hideChest | skinCap.hideChestOverlay) {
            modelPlayerNormal.bipedBodyWear.isHidden = true;
        }

        // Left arm
        if (skinCap.hideArmLeft) {
            modelPlayerNormal.bipedLeftArm.isHidden = true;
        }
        if (skinCap.hideArmLeft | skinCap.hideArmLeftOverlay) {
            modelPlayerNormal.bipedLeftArmwear.isHidden = true;
        }

        // Right arm
        if (skinCap.hideArmRight) {
            modelPlayerNormal.bipedRightArm.isHidden = true;
        }
        if (skinCap.hideArmRight | skinCap.hideArmRightOverlay) {
            modelPlayerNormal.bipedRightArmwear.isHidden = true;
        }

        // Left leg
        if (skinCap.hideLegLeft) {
            modelPlayerNormal.bipedLeftLeg.isHidden = true;
        }
        if (skinCap.hideLegLeft | skinCap.hideLegLeftOverlay) {
            modelPlayerNormal.bipedLeftLegwear.isHidden = true;
        }

        // Right leg
        if (skinCap.hideLegRight) {
            modelPlayerNormal.bipedRightLeg.isHidden = true;
        }
        if (skinCap.hideLegRight | skinCap.hideLegRightOverlay) {
            modelPlayerNormal.bipedRightLegwear.isHidden = true;
        }
        
        bindTexture(getEntityTexture(entity));
        
        modelPlayerNormal.isChild = false;
        modelPlayerNormal.render(entity, 0, 0, 0, 0, 0, scale);
        
        GlStateManager.popMatrix();
    }

    private void renderSkin(Entity entity, ISkinDescriptor skinDescriptor, EntitySkinCapability skinCap, IWardrobeCap wardrobe, IExtraColours extraColours, double distance, boolean doLodLoading) {
        SkinModelRenderHelper modelRenderer = SkinModelRenderHelper.INSTANCE;
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor);

        if (skin != null) {
            if (SkinProperties.PROP_MODEL_OVERRIDE_HEAD.getValue(skin.getProperties())) {
                skinCap.hideHead = true;
            }
            if (SkinProperties.PROP_MODEL_OVERRIDE_CHEST.getValue(skin.getProperties())) {
                skinCap.hideChest = true;
            }
            if (SkinProperties.PROP_MODEL_OVERRIDE_ARM_LEFT.getValue(skin.getProperties())) {
                skinCap.hideArmLeft = true;
            }
            if (SkinProperties.PROP_MODEL_OVERRIDE_ARM_RIGHT.getValue(skin.getProperties())) {
                skinCap.hideArmRight = true;
            }
            if (SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT.getValue(skin.getProperties())) {
                skinCap.hideLegLeft = true;
            }
            if (SkinProperties.PROP_MODEL_OVERRIDE_LEG_RIGHT.getValue(skin.getProperties())) {
                skinCap.hideLegRight = true;
            }

            if (SkinProperties.PROP_MODEL_HIDE_OVERLAY_HEAD.getValue(skin.getProperties())) {
                skinCap.hideHeadOverlay = true;
            }
            if (SkinProperties.PROP_MODEL_HIDE_OVERLAY_CHEST.getValue(skin.getProperties())) {
                skinCap.hideChestOverlay = true;
            }
            if (SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_LEFT.getValue(skin.getProperties())) {
                skinCap.hideArmLeftOverlay = true;
            }
            if (SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_RIGHT.getValue(skin.getProperties())) {
                skinCap.hideArmRightOverlay = true;
            }
            if (SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_LEFT.getValue(skin.getProperties())) {
                skinCap.hideLegLeftOverlay = true;
            }
            if (SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_RIGHT.getValue(skin.getProperties())) {
                skinCap.hideLegRightOverlay = true;
            }

            if (skin.hasPaintData() & ClientProxy.getTexturePaintType() == TexturePaintType.MODEL_REPLACE_AW) {
                if (skin.getSkinType() == SkinTypeRegistry.skinHead) {
                    skinCap.hideHead = true;
                }
                if (skin.getSkinType() == SkinTypeRegistry.skinChest) {
                    skinCap.hideChest = true;
                    skinCap.hideArmLeft = true;
                    skinCap.hideArmRight = true;
                }
                if (skin.getSkinType() == SkinTypeRegistry.skinLegs) {
                    skinCap.hideLegLeft = true;
                    skinCap.hideLegRight = true;
                }
                if (skin.getSkinType() == SkinTypeRegistry.skinFeet) {
                    skinCap.hideLegLeft = true;
                    skinCap.hideLegRight = true;
                }
            }

            SkinDye dye = new SkinDye(wardrobe.getDye());
            for (int i = 0; i < 8; i++) {
                if (skinDescriptor.getSkinDye().haveDyeInSlot(i)) {
                    dye.addDye(i, skinDescriptor.getSkinDye().getDyeColour(i));
                }
            }
            GlStateManager.pushMatrix();
            SkinRenderData renderData = new SkinRenderData(0.0625F, dye, extraColours, distance, doLodLoading, false, false, getEntityTexture(entity));
            modelRenderer.renderEquipmentPart(skin, renderData, entity, modelPlayerNormal);
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return DefaultPlayerSkin.getDefaultSkinLegacy();
    }
}
