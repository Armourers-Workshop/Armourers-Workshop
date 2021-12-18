package moe.plushie.armourers_workshop.core.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinLayerRendererPlayer<T extends LivingEntity, M extends BipedModel<T>> extends LayerRenderer<T, M> {

    public BakedSkin bakedSkin;

    public SkinLayerRendererPlayer(IEntityRenderer<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrix, IRenderTypeBuffer renderer, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        float f = 1f / 16f;
        matrix.pushPose();
        matrix.scale(f, f, f);
        SkinModelRenderer.INSTANCE.renderSkin(bakedSkin, getParentModel(), matrix, renderer);
        matrix.popPose();
    }

    //    @Override
//    public void doRenderLayer(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//        if (GuiTabWardrobeContributor.testMode) {
//            Contributor contributor = Contributors.INSTANCE.getContributor(entitylivingbaseIn.getGameProfile());
//            if (contributor != null) {
//                RenderBlockMannequin.renderMagicCircle(Minecraft.getMinecraft(), contributor.r, contributor.g, contributor.b, partialTicks, 0, false);
//            }
//        }
//
//        double distance = Minecraft.getMinecraft().player.getDistance(entitylivingbaseIn);
//        if (distance > ConfigHandlerClient.renderDistanceSkin) {
//            return;
//        }
//        renderPlayer.getMainModel().bipedLeftArm.isHidden = false;
//        renderPlayer.getMainModel().bipedRightArm.isHidden = false;
//        EntitySkinCapability skinCap = (EntitySkinCapability) EntitySkinCapability.get(entitylivingbaseIn);
//        if (skinCap == null) {
//            return;
//        }
//
//        skinCap.hideHead = false;
//        skinCap.hideChest = false;
//        skinCap.hideArmLeft = false;
//        skinCap.hideArmRight = false;
//        skinCap.hideLegLeft = false;
//        skinCap.hideLegRight = false;
//
//        skinCap.hideHeadOverlay = false;
//        skinCap.hideChestOverlay = false;
//        skinCap.hideArmLeftOverlay = false;
//        skinCap.hideArmRightOverlay = false;
//        skinCap.hideLegLeftOverlay = false;
//        skinCap.hideLegRightOverlay = false;
//
//        ISkinType[] skinTypes = skinCap.getValidSkinTypes();
//        SkinModelRenderHelper modelRenderer = SkinModelRenderHelper.INSTANCE;
//        IExtraColours extraColours = ExtraColours.EMPTY_COLOUR;
//        IPlayerWardrobeCap wardrobe = PlayerWardrobeCap.get(entitylivingbaseIn);
//        if (wardrobe != null) {
//            extraColours = wardrobe.getExtraColours();
//        }
//
//        for (int i = 0; i < skinTypes.length; i++) {
//            ISkinType skinType = skinTypes[i];
//            ISkinDescriptor skinDescriptorArmour = getSkinDescriptorFromArmourer(entitylivingbaseIn, skinType);
//            if (skinDescriptorArmour != null) {
//                renderSkin(entitylivingbaseIn, skinDescriptorArmour, skinCap, wardrobe, extraColours, distance, entitylivingbaseIn != Minecraft.getMinecraft().player);
//            } else {
//                if (skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypes.BIPED_WINGS | skinType == SkinTypes.BIPED_OUTFIT) {
//                    for (int skinIndex = 0; skinIndex < skinCap.getSlotCountForSkinType(skinType); skinIndex++) {
//                        ISkinDescriptor skinDescriptor = skinCap.getSkinDescriptor(skinType, skinIndex);
//                        if (skinDescriptor != null) {
//                            renderSkin(entitylivingbaseIn, skinDescriptor, skinCap, wardrobe, extraColours, distance, entitylivingbaseIn != Minecraft.getMinecraft().player);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private void renderSkin(EntityPlayer entityPlayer, ISkinDescriptor skinDescriptor, EntitySkinCapability skinCap, IWardrobeCap wardrobe, IExtraColours extraColours, double distance, boolean doLodLoading) {
//        SkinModelRenderHelper modelRenderer = SkinModelRenderHelper.INSTANCE;
//        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor);
//
//        if (skin != null) {
//            if (SkinProperty.MODEL_OVERRIDE_HEAD.getValue(skin.getProperties())) {
//                skinCap.hideHead = true;
//            }
//            if (SkinProperty.MODEL_OVERRIDE_CHEST.getValue(skin.getProperties())) {
//                skinCap.hideChest = true;
//            }
//            if (SkinProperty.MODEL_OVERRIDE_ARM_LEFT.getValue(skin.getProperties())) {
//                skinCap.hideArmLeft = true;
//            }
//            if (SkinProperty.MODEL_OVERRIDE_ARM_RIGHT.getValue(skin.getProperties())) {
//                skinCap.hideArmRight = true;
//            }
//            if (SkinProperty.MODEL_OVERRIDE_LEG_LEFT.getValue(skin.getProperties())) {
//                skinCap.hideLegLeft = true;
//            }
//            if (SkinProperty.MODEL_OVERRIDE_LEG_RIGHT.getValue(skin.getProperties())) {
//                skinCap.hideLegRight = true;
//            }
//
//            if (SkinProperty.MODEL_HIDE_OVERLAY_HEAD.getValue(skin.getProperties())) {
//                skinCap.hideHeadOverlay = true;
//            }
//            if (SkinProperty.MODEL_HIDE_OVERLAY_CHEST.getValue(skin.getProperties())) {
//                skinCap.hideChestOverlay = true;
//            }
//            if (SkinProperty.MODEL_HIDE_OVERLAY_ARM_LEFT.getValue(skin.getProperties())) {
//                skinCap.hideArmLeftOverlay = true;
//            }
//            if (SkinProperty.MODEL_HIDE_OVERLAY_ARM_RIGHT.getValue(skin.getProperties())) {
//                skinCap.hideArmRightOverlay = true;
//            }
//            if (SkinProperty.MODEL_HIDE_OVERLAY_LEG_LEFT.getValue(skin.getProperties())) {
//                skinCap.hideLegLeftOverlay = true;
//            }
//            if (SkinProperty.MODEL_HIDE_OVERLAY_LEG_RIGHT.getValue(skin.getProperties())) {
//                skinCap.hideLegRightOverlay = true;
//            }
//
//            if (skin.hasPaintData() & ClientProxy.getTexturePaintType() == TexturePaintType.MODEL_REPLACE_AW) {
//                if (skin.getType() == SkinTypes.BIPED_HEAD) {
//                    skinCap.hideHead = true;
//                }
//                if (skin.getType() == SkinTypes.BIPED_CHEST) {
//                    skinCap.hideChest = true;
//                    skinCap.hideArmLeft = true;
//                    skinCap.hideArmRight = true;
//                }
//                if (skin.getType() == SkinTypes.BIPED_LEGS) {
//                    skinCap.hideLegLeft = true;
//                    skinCap.hideLegRight = true;
//                }
//                if (skin.getType() == SkinTypes.BIPED_FEET) {
//                    skinCap.hideLegLeft = true;
//                    skinCap.hideLegRight = true;
//                }
//            }
//
//            SkinDye dye = new SkinDye(wardrobe.getDye());
//            for (int i = 0; i < 8; i++) {
//                if (skinDescriptor.getSkinDye().haveDyeInSlot(i)) {
//                    dye.addDye(i, skinDescriptor.getSkinDye().getDyeColour(i));
//                }
//            }
//            GlStateManager.pushMatrix();
//            modelRenderer.renderEquipmentPart(skin, new SkinRenderData(0.0625F, dye, extraColours, distance, doLodLoading, false, false, ((AbstractClientPlayer) entityPlayer).getLocationSkin()), entityPlayer, renderPlayer.getMainModel());
//            // modelRenderer.renderEquipmentPart(entityPlayer, renderPlayer.getMainModel(),
//            // skin, dye, extraColours, distance, doLodLoading);
//            GlStateManager.popMatrix();
//        }
//    }
//
//    private ISkinDescriptor getSkinDescriptorFromArmourer(Entity entity, ISkinType skinType) {
//        if (skinType.getVanillaArmourSlotId() >= 0 && skinType.getVanillaArmourSlotId() < 4) {
//            int slot = 3 - skinType.getVanillaArmourSlotId();
//            ItemStack armourStack = ClientWardrobeHandler.getArmourInSlot(slot);
//            return SkinNBTHelper.getSkinDescriptorFromStack(armourStack);
//        }
//        return null;
//    }
//
//    @Override
//    public boolean shouldCombineTextures() {
//        return false;
//    }
}
