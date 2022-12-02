package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

@Available("[1.18, )")
public abstract class AbstractBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    public AbstractBlockEntityRenderer(AbstractBlockEntityRendererContext context) {
    }
}
