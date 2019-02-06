package moe.plushie.armourers_workshop.client.render.entity;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.handler.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.proxies.ClientProxy.SkinRenderType;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkinLayerRendererPlayer implements LayerRenderer<EntityPlayer> {

    private final RenderPlayer renderPlayer;

    public SkinLayerRendererPlayer(RenderPlayer renderPlayer) {
        this.renderPlayer = renderPlayer;
    }

    @Override
    public void doRenderLayer(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (ClientProxy.getSkinRenderType() != SkinRenderType.RENDER_LAYER) {
            return;
        }
        double distance = Minecraft.getMinecraft().player.getDistance(
                entitylivingbaseIn.posX,
                entitylivingbaseIn.posY,
                entitylivingbaseIn.posZ);
        if (distance > ConfigHandlerClient.renderDistanceSkin) {
            return;
        }
        renderPlayer.getMainModel().bipedLeftArm.isHidden = false;
        renderPlayer.getMainModel().bipedRightArm.isHidden = false;
        EntitySkinCapability skinCap = (EntitySkinCapability) EntitySkinCapability.get(entitylivingbaseIn);
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
        
        ISkinType[] skinTypes = skinCap.getValidSkinTypes();
        SkinModelRenderer modelRenderer = SkinModelRenderer.INSTANCE;
        ExtraColours extraColours = ExtraColours.EMPTY_COLOUR;
        IPlayerWardrobeCap wardrobe = PlayerWardrobeCap.get(entitylivingbaseIn);
        if (wardrobe != null) {
            extraColours = wardrobe.getExtraColours();
        }
        
        for (int i = 0; i < skinTypes.length; i++) {
            ISkinType skinType = skinTypes[i];
            ISkinDescriptor skinDescriptorArmour = getSkinDescriptorFromArmourer(entitylivingbaseIn, skinType);
            if (skinDescriptorArmour != null) {
                renderSkin(entitylivingbaseIn, skinDescriptorArmour, skinCap, wardrobe, extraColours, 0, entitylivingbaseIn != Minecraft.getMinecraft().player);
            } else {
                if (skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypeRegistry.skinWings | skinType == SkinTypeRegistry.skinOutfit) {
                    for (int skinIndex = 0; skinIndex < skinCap.getSlotCountForSkinType(skinType); skinIndex++) {
                        ISkinDescriptor skinDescriptor = skinCap.getSkinDescriptor(skinType, skinIndex);
                        if (skinDescriptor != null) {
                            renderSkin(entitylivingbaseIn, skinDescriptor, skinCap, wardrobe, extraColours, 0, entitylivingbaseIn != Minecraft.getMinecraft().player);
                        }
                    }
                }
            }
        }
    }
    
    private void renderSkin(EntityPlayer entityPlayer, ISkinDescriptor skinDescriptor, EntitySkinCapability skinCap, IWardrobeCap wardrobe, ExtraColours extraColours, double distance, boolean doLodLoading) {
        SkinModelRenderer modelRenderer = SkinModelRenderer.INSTANCE;
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
            
            SkinDye dye = new SkinDye(wardrobe.getDye());
            for (int i = 0; i < 8; i++) {
                if (skinDescriptor.getSkinDye().haveDyeInSlot(i)) {
                    dye.addDye(i, skinDescriptor.getSkinDye().getDyeColour(i));
                }
            }
            modelRenderer.renderEquipmentPart(entityPlayer, renderPlayer.getMainModel(), skin, dye, extraColours, distance, doLodLoading);
        }
    }

    private ISkinDescriptor getSkinDescriptorFromArmourer(Entity entity, ISkinType skinType) {
        if (skinType.getVanillaArmourSlotId() >= 0 && skinType.getVanillaArmourSlotId() < 4) {
            int slot = 3 - skinType.getVanillaArmourSlotId();
            ItemStack armourStack = ClientWardrobeHandler.getArmourInSlot(slot);
            return SkinNBTHelper.getSkinDescriptorFromStack(armourStack);
        }
        return null;
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
