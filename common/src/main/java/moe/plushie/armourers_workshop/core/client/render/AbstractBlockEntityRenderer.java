package moe.plushie.armourers_workshop.core.client.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

//#if MC >= 11800
//# public abstract class AbstractBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
//#else
public abstract class AbstractBlockEntityRenderer<T extends BlockEntity> extends BlockEntityRenderer<T> {
//#endif

    public AbstractBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        //#if MC >= 11800
        //#else
        super(blockEntityRenderDispatcher);
        //#endif
    }
}
