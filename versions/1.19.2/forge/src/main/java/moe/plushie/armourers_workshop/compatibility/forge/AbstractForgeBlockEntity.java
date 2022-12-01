package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IBlockEntitySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockEntity;

public interface AbstractForgeBlockEntity extends IForgeBlockEntity {

    static <T extends BlockEntity> T create(BlockEntityType<T> entityType, BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return entityType.create(blockPos, blockState);
    }

    static <T extends BlockEntity> BlockEntityType<T> createType(IBlockEntitySupplier<T> supplier, Block... blocks) {
        BlockEntityType<?>[] entityTypes = {null};
        BlockEntityType<T> entityType = BlockEntityType.Builder.of((blockPos, blockState) -> supplier.create(entityTypes[0], blockPos, blockState), blocks).build(null);
        entityTypes[0] = entityType;
        return entityType;
    }
}
