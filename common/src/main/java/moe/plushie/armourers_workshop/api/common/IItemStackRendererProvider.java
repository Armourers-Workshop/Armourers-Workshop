package moe.plushie.armourers_workshop.api.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

public interface IItemStackRendererProvider {

    @Environment(value = EnvType.CLIENT)
    BlockEntityWithoutLevelRenderer getItemModelRenderer();
}
