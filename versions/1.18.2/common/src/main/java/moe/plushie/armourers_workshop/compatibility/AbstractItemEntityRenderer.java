package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.init.platform.RendererManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

@Environment(value = EnvType.CLIENT)
public abstract class AbstractItemEntityRenderer extends BlockEntityWithoutLevelRenderer {

    public AbstractItemEntityRenderer() {
        this(RendererManager.getBlockContext());
    }

    protected AbstractItemEntityRenderer(AbstractBlockEntityRendererContext context) {
        super(context.getBlockEntityRenderDispatcher(), context.getModelSet());
    }
}
