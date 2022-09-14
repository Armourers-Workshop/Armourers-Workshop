package moe.plushie.armourers_workshop.compatibility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

@Environment(value= EnvType.CLIENT)
public abstract class AbstractItemEntityRenderer extends BlockEntityWithoutLevelRenderer {

    public AbstractItemEntityRenderer() {
    }
}
