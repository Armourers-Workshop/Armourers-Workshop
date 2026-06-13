package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.client.render.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.render.state.MannequinRenderState;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRenderer;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.world.item.ItemStack;

@OnlyIn(Dist.CLIENT)
public class SkinItemRenderer extends SpecialModelRenderer<ItemStack> {

    public static final IDataMapCodec<SkinItemRenderer> MAP_CODEC = IDataMapCodec.unit(SkinItemRenderer::new);

    private static ItemStack playerMannequinItem;
    private static MannequinModel model;

    @Override
    protected void abi$render(ItemStack data, OpenItemDisplayContext itemDisplayContext, int lightmap, int overlay, IGraphicsContext context) {
        // nop
    }

    @Override
    protected ItemStack abi$extractArgument(ItemStack itemStack) {
        return itemStack;
    }

    public static MannequinModel getMannequinModel() {
        if (model == null) {
            model = MannequinModel.placeholder();
            model.setupDefault(MannequinRenderState.getPlaceholder());
        }
        return model;
    }

    public static ItemStack getPlayerMannequinItem() {
        if (playerMannequinItem == null) {
            var entityData = new MannequinEntity.EntityData();
            entityData.setTexture(PlayerSkinDescriptor.fromProfile(EnvironmentManager.getClientUser()));
            playerMannequinItem = entityData.itemStack();
        }
        return playerMannequinItem;
    }
}
