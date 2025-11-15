package moe.plushie.armourers_workshop.compat.fabric.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.compat.builder.AbstractBlockEntityTypeBuilder;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

@Available("[1.18, )")
public class AbstractFabricBlockEntityTypeBuilder<T extends BlockEntity> extends AbstractBlockEntityTypeBuilder<T> {

    public AbstractFabricBlockEntityTypeBuilder(IBlockEntityType.Serializer<T> serializer) {
        super(serializer);
    }

    @Override
    public IBlockEntityType<T> build(OpenResourceLocation registryName) {
        return new Proxy<T>(create(registryName)) {
            @Override
            public T create(BlockGetter level, BlockPos blockPos, BlockState blockState) {
                return get().create(blockPos, blockState);
            }
        };
    }

    private BlockEntityType<T> create(OpenResourceLocation registryName) {
        Block[] blocks1 = blocks.stream().map(Supplier::get).toArray(Block[]::new);
        BlockEntityType<?>[] entityTypes = {null};
        BlockEntityType<T> entityType = FabricBlockEntityTypeBuilder.create((blockPos, blockState) -> serializer.create(entityTypes[0], blockPos, blockState), blocks1).build();
        entityTypes[0] = entityType;
        return entityType;
    }
}
