package moe.plushie.armourers_workshop.compatibility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(value= EnvType.CLIENT)
public abstract class AbstractBlockEntityRenderer<T extends BlockEntity> extends BlockEntityRenderer<T> {

    public AbstractBlockEntityRenderer(AbstractBlockEntityRendererContext context) {
        super(context.getBlockEntityRenderDispatcher());
    }
}
