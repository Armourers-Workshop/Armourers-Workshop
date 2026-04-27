package moe.plushie.armourers_workshop.compat.forge.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.compat.builder.AbstractBlockEntityTypeBuilder;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeBlockEntity;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Available("[18, 26)")
public class AbstractForgeBlockEntityTypeBuilder<T extends BlockEntity> extends AbstractBlockEntityTypeBuilder<T> {

    public AbstractForgeBlockEntityTypeBuilder(IBlockEntityType.Serializer<T> serializer) {
        super(serializer);
    }

    @Override
    public IBlockEntityType<T> build(OpenResourceKey registryName) {
        return new Proxy<>(create(registryName)) {
            @Override
            public T create(BlockGetter level, BlockPos blockPos, BlockState blockState) {
                return AbstractForgeBlockEntity.create(get(), level, blockPos, blockState);
            }
        };
    }

    private BlockEntityType<T> create(OpenResourceKey registryName) {
        var blocks1 = blocks.stream().map(Supplier::get).toArray(Block[]::new);
        var entityType = new AtomicReference<BlockEntityType<T>>();
        entityType.set(BlockEntityType.Builder.of((blockPos, blockState) -> serializer.create(entityType.get(), blockPos, blockState), blocks1).build(null));
        return entityType.get();
    }
}
