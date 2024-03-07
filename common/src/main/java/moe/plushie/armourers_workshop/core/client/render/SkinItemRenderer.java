package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractItemStackRenderer;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.item.MannequinItem;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.auto;

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
    public void renderByItem(ItemStack itemStack, AbstractItemTransformType transformType, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        // nop
    }

    public MannequinModel<?> getMannequinModel() {
        auto entity = PlaceholderManager.MANNEQUIN.get();
        if (model == null && entity != null) {
            model = new MannequinModel<>();
            model.young = false;
            model.crouching = false;
            model.riding = false;
            model.prepareMobModel(entity, 0, 0, 0);
            model.setupAnim(entity, 0, 0, 0, 0, 0);
        }
        return model;
    }

    public ItemStack getPlayerMannequinItem() {
        if (playerMannequinItem == null) {
            auto player = EnvironmentManager.getPlayer();
            if (player == null) {
                return ItemStack.EMPTY;
            }
            playerMannequinItem = MannequinItem.of(player, 1.0f);
        }
        return playerMannequinItem;
    }
}
