package moe.plushie.armourers_workshop.compatibility.forge;

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

    static <T extends BlockEntity> BlockEntityType<T> createType(IBlockEntityType.Serializer<T> supplier, Block... blocks) {
        BlockEntityType<?>[] entityTypes = {null};
        var entityType = BlockEntityType.Builder.of((blockPos, blockState) -> supplier.create(entityTypes[0], blockPos, blockState), blocks).build(null);
        entityTypes[0] = entityType;
        return entityType;
    }

    default AABB getRenderBoundingBox() {
        return null;
    }
}
