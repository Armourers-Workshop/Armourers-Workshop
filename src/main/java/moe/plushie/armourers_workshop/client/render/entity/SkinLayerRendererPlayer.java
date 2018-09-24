package moe.plushie.armourers_workshop.client.render.entity;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.skin.ExPropsPlayerSkinData;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.proxies.ClientProxy.SkinRenderType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
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
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(entitylivingbaseIn);
        if (skinCapability == null) {
            return;
        }
        ISkinType[] skinTypes = skinCapability.getValidSkinTypes();
        SkinModelRenderer modelRenderer = SkinModelRenderer.INSTANCE;
        byte[] extraColours = null;
        for (int i = 0; i < skinTypes.length; i++) {
            ISkinType skinType = skinTypes[i];
            for (int skinIndex = 0; skinIndex < ExPropsPlayerSkinData.MAX_SLOTS_PER_SKIN_TYPE; skinIndex++) {
                Skin skin = modelRenderer.getPlayerCustomArmour(entitylivingbaseIn, skinType, skinIndex);
                if (skin == null) {
                    continue;
                }
                ISkinDye skinDye = modelRenderer.getPlayerDyeData(entitylivingbaseIn, skinType, skinIndex);
                
                modelRenderer.renderEquipmentPart(entitylivingbaseIn, renderPlayer.getMainModel(), skin, skinDye, extraColours, 0, true);
            }
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
