package moe.plushie.armourers_workshop.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractItemStackRenderer;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.item.MannequinItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
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
    public void renderByItem(ItemStack itemStack, AbstractItemTransformType transformType, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
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
            auto player = Minecraft.getInstance().player;
            if (player == null) {
                return ItemStack.EMPTY;
            }
            playerMannequinItem = MannequinItem.of(player, 1.0f);
        }
        return playerMannequinItem;
    }
}
