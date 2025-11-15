package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;

@Available("[1.21, )")
public interface AbstractForgeBlockEntity extends IBlockEntityExtension {

    static <T extends BlockEntity> T create(BlockEntityType<T> entityType, BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return entityType.create(blockPos, blockState);
    }

    default AABB getRenderBoundingBox() {
        return null;
    }
}
