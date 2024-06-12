package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBinder;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeBlockEntity;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.TypedRegistry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;
import java.util.function.Supplier;

public class BlockEntityTypeBuilderImpl<T extends BlockEntity> implements IBlockEntityTypeBuilder<T> {

    private IRegistryBinder<BlockEntityType<T>> binder;
    private final LinkedList<Supplier<Block>> blocks = new LinkedList<>();
    private final IBlockEntityType.Serializer<T> supplier;

    public BlockEntityTypeBuilderImpl(IBlockEntityType.Serializer<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public IBlockEntityTypeBuilder<T> of(Supplier<Block> block) {
        this.blocks.add(block);
        return this;
    }

    @Override
    public IBlockEntityTypeBuilder<T> bind(Supplier<AbstractBlockEntityRendererProvider<T>> provider) {
        this.binder = () -> blockEntityType -> {
            // here is safe call client registry.
            GameRenderer.registerBlockEntityRendererFO(blockEntityType, provider.get());
        };
        return this;
    }

    @Override
    public IRegistryHolder<IBlockEntityType<T>> build(String name) {
        IRegistryHolder<BlockEntityType<T>> object = AbstractForgeRegistries.BLOCK_ENTITY_TYPES.register(name, () -> {
            Block[] blocks1 = blocks.stream().map(Supplier::get).toArray(Block[]::new);
            return AbstractForgeBlockEntity.createType(supplier, blocks1);
        });
        Proxy<T> proxy = new Proxy<>(object);
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT, IRegistryBinder.perform(binder, object));
        return TypedRegistry.Entry.of(object.getRegistryName(), () -> proxy);
    }

    public static class Proxy<T extends BlockEntity> implements IBlockEntityType<T> {

        private final IRegistryHolder<BlockEntityType<T>> object;

        public Proxy(IRegistryHolder<BlockEntityType<T>> object) {
            this.object = object;
        }

        @Override
        public T create(BlockGetter level, BlockPos blockPos, BlockState blockState) {
            return AbstractForgeBlockEntity.create(object.get(), level, blockPos, blockState);
        }

        @Override
        public BlockEntityType<T> get() {
            return object.get();
        }
    }
}
