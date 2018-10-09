package moe.plushie.armourers_workshop.client.render.entity;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.handler.EquipmentWardrobeHandler;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.proxies.ClientProxy.SkinRenderType;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.model.ModelBiped;
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
        renderPlayer.getMainModel().bipedLeftArm.isHidden = false;
        renderPlayer.getMainModel().bipedRightArm.isHidden = false;
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(entitylivingbaseIn);
        if (skinCapability == null) {
            return;
        }
        ISkinType[] skinTypes = skinCapability.getValidSkinTypes();
        SkinModelRenderer modelRenderer = SkinModelRenderer.INSTANCE;
        byte[] extraColours = null;
        for (int i = 0; i < skinTypes.length; i++) {
            ISkinType skinType = skinTypes[i];
            
            ISkinDescriptor skinDescriptorArmour = getSkinDescriptorFromArmourer(entitylivingbaseIn, skinType);
            if (skinDescriptorArmour != null) {
                renderSkin(entitylivingbaseIn, renderPlayer.getMainModel(), skinDescriptorArmour, extraColours, 0, true);
            } else {
                if (skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypeRegistry.skinWings) {
                    for (int skinIndex = 0; skinIndex < skinCapability.getSlotCountForSkinType(skinType); skinIndex++) {
                        ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(skinType, skinIndex);
                        if (skinDescriptor != null) {
                            renderSkin(entitylivingbaseIn, renderPlayer.getMainModel(), skinDescriptor, extraColours, 0, true);
                        }
                    }
                }
            }
        }
    }

    private void renderSkin(Entity entity, ModelBiped modelBiped, ISkinDescriptor skinDescriptor, byte[] extraColours, double distance, boolean doLodLoading) {
        SkinModelRenderer modelRenderer = SkinModelRenderer.INSTANCE;
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor);
        if (skin != null) {
            modelRenderer.renderEquipmentPart(entity, renderPlayer.getMainModel(), skin, skinDescriptor.getSkinDye(), extraColours, distance, doLodLoading);
        }
    }

    private ISkinDescriptor getSkinDescriptorFromArmourer(Entity entity, ISkinType skinType) {
        if (skinType.getVanillaArmourSlotId() >= 0 && skinType.getVanillaArmourSlotId() < 4) {
            int slot = 3 - skinType.getVanillaArmourSlotId();
            ItemStack armourStack = EquipmentWardrobeHandler.getArmourInSlot(slot);
            return SkinNBTHelper.getSkinDescriptorFromStack(armourStack);
        }
        return null;
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
