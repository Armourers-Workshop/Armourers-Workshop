package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.api.client.state.IBlockEntityRenderState;
import moe.plushie.armourers_workshop.compat.api.blockentity.BlockEntityAccessor;
import net.minecraft.core.BlockPos;

public class BlockEntityRenderState extends RenderState implements IBlockEntityRenderState {

    protected BlockPos blockPos;

    public BlockPos blockPos() {
        return blockPos;
    }

    public static void extract(BlockEntityAccessor entity, BlockEntityRenderState renderState) {
        renderState.blockPos = entity.aw2$getBlockPos();
    }
}
