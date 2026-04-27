package moe.plushie.armourers_workshop.compat.fabric.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.compat.builder.AbstractBlockEntityTypeBuilder;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Available("[18, )")
public class AbstractFabricBlockEntityTypeBuilder<T extends BlockEntity> extends AbstractBlockEntityTypeBuilder<T> {

    public AbstractFabricBlockEntityTypeBuilder(IBlockEntityType.Serializer<T> serializer) {
        super(serializer);
    }

    @Override
    public IBlockEntityType<T> build(OpenResourceKey registryName) {
        return new Proxy<T>(create(registryName)) {
            @Override
            public T create(BlockGetter level, BlockPos blockPos, BlockState blockState) {
                return get().create(blockPos, blockState);
            }
        };
    }

    private BlockEntityType<T> create(OpenResourceKey registryName) {
        var blocks1 = blocks.stream().map(Supplier::get).toArray(Block[]::new);
        var entityType = new AtomicReference<BlockEntityType<T>>();
        entityType.set(FabricBlockEntityTypeBuilder.create((blockPos, blockState) -> serializer.create(entityType.get(), blockPos, blockState), blocks1).build());
        return entityType.get();
    }
}
