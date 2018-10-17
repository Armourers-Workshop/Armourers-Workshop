package moe.plushie.armourers_workshop.client.render.entity;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.handler.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
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
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(entitylivingbaseIn);
        if (skinCapability == null) {
            return;
        }
        ISkinType[] skinTypes = skinCapability.getValidSkinTypes();
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
                renderSkin(entitylivingbaseIn, skinDescriptorArmour, wardrobe, extraColours, 0, entitylivingbaseIn != Minecraft.getMinecraft().player);
            } else {
                if (skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypeRegistry.skinWings) {
                    for (int skinIndex = 0; skinIndex < skinCapability.getSlotCountForSkinType(skinType); skinIndex++) {
                        ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(skinType, skinIndex);
                        if (skinDescriptor != null) {
                            renderSkin(entitylivingbaseIn, skinDescriptor, wardrobe, extraColours, 0, entitylivingbaseIn != Minecraft.getMinecraft().player);
                        }
                    }
                }
            }
        }
    }

    private void renderSkin(EntityPlayer entityPlayer, ISkinDescriptor skinDescriptor, IWardrobeCap wardrobe, ExtraColours extraColours, double distance, boolean doLodLoading) {
        // Fix to stop head skins rendering when using the Real First-Person Render mod.
        if (entityPlayer.equals(Minecraft.getMinecraft().player) & skinDescriptor.getIdentifier().getSkinType() == SkinTypeRegistry.skinHead) {
            if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
                StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
                for (int i = 0; i < traceElements.length; i++) {
                    StackTraceElement  traceElement = traceElements[i];
                    if (traceElement.toString().contains("realrender")) {
                        return;
                    }
                }
            }
        }
        SkinModelRenderer modelRenderer = SkinModelRenderer.INSTANCE;
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor);
        if (skin != null) {
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
