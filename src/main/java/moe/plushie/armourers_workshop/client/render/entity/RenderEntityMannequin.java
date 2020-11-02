package moe.plushie.armourers_workshop.client.render.entity;

import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.GL11;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.model.ModelHelper;
import moe.plushie.armourers_workshop.client.render.EntityTextureInfo;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinRenderData;
import moe.plushie.armourers_workshop.client.render.tileentities.RenderBlockMannequin;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.client.texture.PlayerTexture;
import moe.plushie.armourers_workshop.common.Contributors;
import moe.plushie.armourers_workshop.common.Contributors.Contributor;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations.BipedPart;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.holiday.ModHolidays;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.proxies.ClientProxy.TexturePaintType;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityMannequin extends Render<EntityMannequin> {

    private static LoadingCache<EntityMannequin, EntityTextureInfo> textureCache;

    private boolean isHalloweenSeason;
    private boolean isHalloween;

    private final ModelPlayer modelPlayerSmall = new ModelPlayer(0F, true);
    private final ModelPlayer modelPlayerNormal = new ModelPlayer(0F, false);

    public RenderEntityMannequin(RenderManager renderManager) {
        super(renderManager);
        if (textureCache == null) {
            CacheBuilder builder = CacheBuilder.newBuilder();
            builder.expireAfterAccess(1, TimeUnit.MINUTES);
            builder.maximumSize(200);
            builder.removalListener(new RemovalListener<EntityMannequin, EntityTextureInfo>() {

                @Override
                public void onRemoval(RemovalNotification<EntityMannequin, EntityTextureInfo> notification) {

                    Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                        @Override
                        public void run() {
                            notification.getValue().deleteTexture();
                        }
                    });
                }
            });
            textureCache = builder.<EntityMannequin, EntityTextureInfo>build(new CacheLoader<EntityMannequin, EntityTextureInfo>() {

                @Override
                public EntityTextureInfo load(EntityMannequin key) throws Exception {
                    return new EntityTextureInfo();
                }
            });
        }
    }

    @Override
    public void doRender(EntityMannequin entity, double x, double y, double z, float entityYaw, float partialTicks) {
        EntitySkinCapability skinCap = (EntitySkinCapability) EntitySkinCapability.get(entity);
        if (skinCap == null) {
            return;
        }

        isHalloweenSeason = ModHolidays.HALLOWEEN_SEASON.isHolidayActive();
        isHalloween = ModHolidays.HALLOWEEN.isHolidayActive();

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        float scale = 0.0625F;
        float size = entity.getScale();

        GlStateManager.translate(x, y, z);

        GlStateManager.scale(15F * scale, -15F * scale, -15F * scale);

        GlStateManager.scale(size, size, size);
        GlStateManager.translate(0, -24F * scale, 0);

        GlStateManager.rotate(entity.getRotation(), 0F, 1F, 0F);

        GlStateManager.disableNormalize();
        GlStateManager.disableLighting();
        GlStateManager.disableRescaleNormal();

        GlStateManager.enableNormalize();
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();

        RenderHelper.enableStandardItemLighting();

        // GlStateManager.enableLighting();
        // GlStateManager.disableDepth();

        // GlStateManager.scale(1F, 0.7F, 1F);

        // super.doRender(entity, x, y, z, entityYaw, partialTicks);

        PlayerTexture playerTexture = ClientProxy.playerTextureDownloader.getPlayerTexture(entity.getTextureData());

        ModelPlayer targetModel = modelPlayerNormal;
        if (playerTexture.isSlimModel()) {
            targetModel = modelPlayerSmall;
        }

        targetModel.setRotationAngles(0F, 0F, 0F, 0F, 0F, scale, entity);
        BipedRotations bipedRotations = entity.getBipedRotations();
        bipedRotations.applyRotationsToBiped(targetModel);

        float[] bodyRots = bipedRotations.getPartRotations(BipedPart.CHEST);

        GlStateManager.rotate((float) Math.toDegrees(bodyRots[0]), 1F, 0F, 0F);
        GlStateManager.rotate((float) Math.toDegrees(bodyRots[1]), 0F, 1F, 0F);
        GlStateManager.rotate((float) Math.toDegrees(bodyRots[2]), 0F, 0F, 1F);

        targetModel.bipedBody.rotateAngleX = 0F;
        targetModel.bipedBody.rotateAngleY = 0F;
        targetModel.bipedBody.rotateAngleZ = 0F;

        targetModel.bipedBodyWear.rotateAngleX = 0F;
        targetModel.bipedBodyWear.rotateAngleY = 0F;
        targetModel.bipedBodyWear.rotateAngleZ = 0F;

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

        targetModel.bipedHead.isHidden = false;
        targetModel.bipedHeadwear.isHidden = false;
        targetModel.bipedBody.isHidden = false;
        targetModel.bipedBodyWear.isHidden = false;
        targetModel.bipedLeftArm.isHidden = false;
        targetModel.bipedLeftArmwear.isHidden = false;
        targetModel.bipedRightArm.isHidden = false;
        targetModel.bipedRightArmwear.isHidden = false;
        targetModel.bipedLeftLeg.isHidden = false;
        targetModel.bipedLeftLegwear.isHidden = false;
        targetModel.bipedRightLeg.isHidden = false;
        targetModel.bipedRightLegwear.isHidden = false;

        ISkinType[] skinTypes = skinCap.getValidSkinTypes();
        SkinModelRenderHelper modelRenderer = SkinModelRenderHelper.INSTANCE;
        IExtraColours extraColours = ExtraColours.EMPTY_COLOUR;
        IWardrobeCap wardrobe = WardrobeCap.get(entity);
        if (wardrobe != null) {
            extraColours = wardrobe.getExtraColours();
        }

        double distance = entity.getDistance(Minecraft.getMinecraft().player);
        // Render skins.
        for (int i = 0; i < skinTypes.length; i++) {
            ISkinType skinType = skinTypes[i];
            if (skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypeRegistry.skinWings | skinType == SkinTypeRegistry.skinOutfit) {
                for (int skinIndex = 0; skinIndex < skinCap.getSlotCountForSkinType(skinType); skinIndex++) {
                    ISkinDescriptor skinDescriptor = skinCap.getSkinDescriptor(skinType, skinIndex);
                    if (skinDescriptor != null) {
                        renderSkin(entity, skinDescriptor, skinCap, wardrobe, extraColours, distance, true, targetModel);
                    }
                }
            }
        }

        // Head
        if (skinCap.hideHead) {
            targetModel.bipedHead.isHidden = true;
        }
        if (skinCap.hideHead | skinCap.hideHeadOverlay) {
            targetModel.bipedHeadwear.isHidden = true;
        }

        // Chest
        if (skinCap.hideChest) {
            targetModel.bipedBody.isHidden = true;

        }
        if (skinCap.hideChest | skinCap.hideChestOverlay) {
            targetModel.bipedBodyWear.isHidden = true;
        }

        // Left arm
        if (skinCap.hideArmLeft) {
            targetModel.bipedLeftArm.isHidden = true;
        }
        if (skinCap.hideArmLeft | skinCap.hideArmLeftOverlay) {
            targetModel.bipedLeftArmwear.isHidden = true;
        }

        // Right arm
        if (skinCap.hideArmRight) {
            targetModel.bipedRightArm.isHidden = true;
        }
        if (skinCap.hideArmRight | skinCap.hideArmRightOverlay) {
            targetModel.bipedRightArmwear.isHidden = true;
        }

        // Left leg
        if (skinCap.hideLegLeft) {
            targetModel.bipedLeftLeg.isHidden = true;
        }
        if (skinCap.hideLegLeft | skinCap.hideLegLeftOverlay) {
            targetModel.bipedLeftLegwear.isHidden = true;
        }

        // Right leg
        if (skinCap.hideLegRight) {
            targetModel.bipedRightLeg.isHidden = true;
        }
        if (skinCap.hideLegRight | skinCap.hideLegRightOverlay) {
            targetModel.bipedRightLegwear.isHidden = true;
        }

        // Build texture.
        EntityTextureInfo textureInfo = textureCache.getUnchecked(entity);
        textureCache.cleanUp();
        buildTexture(playerTexture.getResourceLocation(), textureInfo, skinCap, wardrobe);
        bindTexture(textureInfo.preRender());

        // Render main model.
        if (entity.isVisible()) {
            ModRenderHelper.enableAlphaBlend();

            if (bipedRotations.isChild()) {
                ModelHelper.enableChildModelScale(true, scale);
            }
            targetModel.bipedHead.render(scale);
            targetModel.bipedHeadwear.render(scale);
            if (bipedRotations.isChild()) {
                ModelHelper.disableChildModelScale();
            }

            if (bipedRotations.isChild()) {
                ModelHelper.enableChildModelScale(false, scale);
            }

            targetModel.bipedBody.render(scale);
            targetModel.bipedBodyWear.render(scale);

            targetModel.bipedLeftArm.render(scale);
            targetModel.bipedLeftArmwear.render(scale);

            targetModel.bipedRightArm.render(scale);
            targetModel.bipedRightArmwear.render(scale);

            targetModel.bipedLeftLeg.render(scale);
            targetModel.bipedLeftLegwear.render(scale);

            targetModel.bipedRightLeg.render(scale);
            targetModel.bipedRightLegwear.render(scale);
            if (bipedRotations.isChild()) {
                ModelHelper.disableChildModelScale();
            }
            // targetModel.render(entity, 0, 0, 0, 0, 0, scale);
            ModRenderHelper.disableAlphaBlend();
        }

        for (int i = 0; i < 2; i++) {
            boolean leftArm = i == 0;
            ISkinDescriptor descriptor = null;
            if (leftArm) {
                descriptor = SkinNBTHelper.getSkinDescriptorFromStack(entity.getHandLeft());
            } else {
                descriptor = SkinNBTHelper.getSkinDescriptorFromStack(entity.getHandRight());
            }
            if (descriptor != null) {
                GlStateManager.pushMatrix();
                if (bipedRotations.isChild()) {
                    ModelHelper.enableChildModelScale(false, scale);
                }

                float[] armRots = bipedRotations.getPartRotations(BipedPart.RIGHT_ARM);

                if (leftArm) {
                    armRots = bipedRotations.getPartRotations(BipedPart.LEFT_ARM);
                }

                if (!leftArm) {
                    GlStateManager.translate(scale * -5F, scale * 2F, scale * 0);
                } else {
                    GlStateManager.translate(scale * 5, scale * 2, scale * 0);
                }

                if (playerTexture.isSlimModel()) {
                    GlStateManager.translate(0, scale * 0.5F, 0);
                }

                GlStateManager.rotate((float) Math.toDegrees(armRots[2]), 0, 0, 1);
                GlStateManager.rotate((float) Math.toDegrees(armRots[1]), 0, 1, 0);
                GlStateManager.rotate((float) Math.toDegrees(armRots[0]), 1, 0, 0);

                if (!leftArm) {
                    GlStateManager.translate(scale * -1F, scale * 8, scale * 0);
                    if (playerTexture.isSlimModel()) {
                        GlStateManager.translate(scale * 0.5F, 0, 0);
                    }
                } else {
                    GlStateManager.translate(scale * 1, scale * 8, scale * 0);
                    if (playerTexture.isSlimModel()) {
                        GlStateManager.translate(scale * -0.5F, 0, 0);
                    }
                }

                GlStateManager.rotate(90, 1, 0, 0);

                if (leftArm) {
                    GlStateManager.scale(-1, 1, 1);
                    GlStateManager.cullFace(CullFace.FRONT);
                }
                // GlStateManager.translate((flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
                if (playerTexture.isSlimModel()) {
                    GL11.glScaled(0.75F, 1F, 1F);
                }

                renderSkin(entity, descriptor, skinCap, wardrobe, extraColours, 0, true, null);

                if (leftArm) {
                    GlStateManager.cullFace(CullFace.BACK);
                }

                if (bipedRotations.isChild()) {
                    ModelHelper.disableChildModelScale();
                }
                GlStateManager.popMatrix();
            }
        }

        // ModLogger.log(entity.getTextureData());

        // Render magic circle.
        if (entity.isRenderExtras() & entity.isVisible() & entity.getTextureData().getTextureType() == TextureType.USER) {
            Contributor contributor = Contributors.INSTANCE.getContributor(entity.getTextureData().getProfile());
            if (contributor != null) {
                int offset = entity.getEntityId() * 31;
                RenderBlockMannequin.renderMagicCircle(Minecraft.getMinecraft(), contributor.r, contributor.g, contributor.b, partialTicks, offset, targetModel.isChild);
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private void renderSkin(EntityMannequin entity, ISkinDescriptor skinDescriptor, EntitySkinCapability skinCap, IWardrobeCap wardrobe, IExtraColours extraColours, double distance, boolean doLodLoading, ModelPlayer targetModel) {
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
            modelRenderer.renderEquipmentPart(skin, renderData, entity, targetModel);
            GlStateManager.popMatrix();
        }
    }

    private void buildTexture(ResourceLocation texture, EntityTextureInfo textureInfo, IEntitySkinCapability skinCapability, IWardrobeCap wardrobeCap) {
        textureInfo.updateTexture(texture);

        textureInfo.updateExtraColours(wardrobeCap.getExtraColours());

        ISkinType[] skinTypes = new ISkinType[] { SkinTypeRegistry.skinHead, SkinTypeRegistry.skinChest, SkinTypeRegistry.skinLegs, SkinTypeRegistry.skinFeet, SkinTypeRegistry.skinOutfit };

        Skin[] skins = new Skin[skinTypes.length * EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE];
        ISkinDye[] dyes = new ISkinDye[skinTypes.length * EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE];

        for (int skinIndex = 0; skinIndex < EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE; skinIndex++) {
            Skin[] skin = new Skin[skinTypes.length];
            ISkinDye[] dye = new ISkinDye[skinTypes.length];

            for (int i = 0; i < skinTypes.length; i++) {
                ISkinDescriptor descriptor = skinCapability.getSkinDescriptor(skinTypes[i], skinIndex);
                if (descriptor != null) {
                    skin[i] = ClientSkinCache.INSTANCE.getSkin(descriptor, false);
                    dye[i] = descriptor.getSkinDye();
                }
            }

            skins[0 + skinIndex * skinTypes.length] = skin[0];
            skins[1 + skinIndex * skinTypes.length] = skin[1];
            skins[2 + skinIndex * skinTypes.length] = skin[2];
            skins[3 + skinIndex * skinTypes.length] = skin[3];
            skins[4 + skinIndex * skinTypes.length] = skin[4];

            dyes[0 + skinIndex * skinTypes.length] = mixDye(wardrobeCap.getDye(), dye[0]);
            dyes[1 + skinIndex * skinTypes.length] = mixDye(wardrobeCap.getDye(), dye[1]);
            dyes[2 + skinIndex * skinTypes.length] = mixDye(wardrobeCap.getDye(), dye[2]);
            dyes[3 + skinIndex * skinTypes.length] = mixDye(wardrobeCap.getDye(), dye[3]);
            dyes[4 + skinIndex * skinTypes.length] = mixDye(wardrobeCap.getDye(), dye[4]);
        }

        textureInfo.updateSkins(skins);
        textureInfo.updateDyes(dyes);
    }

    private ISkinDye mixDye(ISkinDye wardrobeDye, ISkinDye itemDye) {
        SkinDye dye = new SkinDye(wardrobeDye);
        if (itemDye != null) {
            for (int i = 0; i < 8; i++) {
                if (itemDye.haveDyeInSlot(i)) {
                    dye.addDye(i, itemDye.getDyeColour(i));
                }
            }
        }
        return dye;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMannequin entity) {
        PlayerTexture playerTexture = ClientProxy.playerTextureDownloader.getPlayerTexture(entity.getTextureData());
        return playerTexture.getResourceLocation();
    }
}
