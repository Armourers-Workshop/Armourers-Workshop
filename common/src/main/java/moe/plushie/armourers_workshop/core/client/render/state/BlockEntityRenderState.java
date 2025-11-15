package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.api.client.state.IBlockEntityRenderState;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityRenderState extends RenderState implements IBlockEntityRenderState {

    protected BlockPos blockPos;

    public BlockPos blockPos() {
        return blockPos;
    }

    public static void extract(BlockEntity entity, BlockEntityRenderState renderState) {
        renderState.blockPos = entity.getBlockPos();
    }
}
