package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractItemStackRenderer;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class SkinItemRenderer extends AbstractItemStackRenderer {

    private static SkinItemRenderer INSTANCE;

    private ItemStack playerMannequinItem;
    private MannequinModel<MannequinEntity> model;

    public static SkinItemRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkinItemRenderer();
        }
        return INSTANCE;
    }

    @Override
    public void renderByItem(ItemStack itemStack, OpenItemDisplayContext itemDisplayContext, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        // nop
    }

    public MannequinModel<?> mannequinModel() {
        var entity = PlaceholderManager.MANNEQUIN.get();
        if (model == null && entity != null) {
            model = MannequinModel.placeholder();
            model.young = false;
            model.crouching = false;
            model.riding = false;
            model.prepareMobModel(entity, 0, 0, 0);
            model.setupAnim(entity, 0, 0, 0, 0, 0);
        }
        return model;
    }

    public ItemStack playerMannequinItem() {
        if (playerMannequinItem == null) {
            var player = EnvironmentManager.getPlayer();
            if (player == null) {
                return ItemStack.EMPTY;
            }
            var entityData = new MannequinEntity.EntityData();
            entityData.setTexture(EntityTextureDescriptor.fromProfile(player.getGameProfile()));
            playerMannequinItem = entityData.itemStack();
        }
        return playerMannequinItem;
    }
}
