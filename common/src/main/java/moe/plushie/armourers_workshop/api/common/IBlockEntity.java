package moe.plushie.armourers_workshop.api.common;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public interface IBlockEntity {

//    boolean isRemoved();
//
//    Level getLevel();
//
//    BlockPos getBlockPos();
//
//    BlockState getBlockState();

//    @Nullable
//    <T> T getCapability(IBlockEntityCapability<T> capability, @Nullable Direction dir);

    /**
     * Return an {@link AABB} that controls the visible scope of a {@link BlockEntityRenderer} associated with this {@link BlockEntity}
     * at this location.
     *
     * @return an appropriately size {@link AABB} for the {@link BlockEntity}
     */
    default AABB getVisibleBox(BlockState blockState) {
        return null;
    }
}
