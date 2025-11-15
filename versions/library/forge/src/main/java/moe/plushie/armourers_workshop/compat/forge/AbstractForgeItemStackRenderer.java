package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

@Available("[1.21, 1.22)")
public interface AbstractForgeItemStackRenderer extends IClientItemExtensions {

    BlockEntityWithoutLevelRenderer getItemStackRenderer();

    @Override
    default BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return getItemStackRenderer();
    }
}
