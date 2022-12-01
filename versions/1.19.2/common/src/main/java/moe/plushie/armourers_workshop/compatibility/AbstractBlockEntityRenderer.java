package moe.plushie.armourers_workshop.compatibility;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    public AbstractBlockEntityRenderer(AbstractBlockEntityRendererContext context) {
    }
}
