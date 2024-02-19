package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBinder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricBlockEntity;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.TypedRegistry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
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
            GameRenderer.registerBlockEntityRendererFA(blockEntityType, provider.get());
        };
        return this;
    }


    @Override
    public IRegistryKey<IBlockEntityType<T>> build(String name) {
        IRegistryKey<BlockEntityType<T>> object = Registry.registerBlockEntityTypeFA(name, () -> {
            Block[] blocks1 = blocks.stream().map(Supplier::get).toArray(Block[]::new);
            return AbstractFabricBlockEntity.createType(supplier, blocks1);
        });
        Proxy<T> proxy = new Proxy<>(object);
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT, IRegistryBinder.perform(binder, object));
        return TypedRegistry.Entry.ofValue(object.getRegistryName(), proxy);
    }

    public static class Proxy<T extends BlockEntity> implements IBlockEntityType<T> {

        private final IRegistryKey<BlockEntityType<T>> object;

        public Proxy(IRegistryKey<BlockEntityType<T>> object) {
            this.object = object;
        }

        @Override
        public T create(BlockGetter level, BlockPos blockPos, BlockState blockState) {
            return AbstractFabricBlockEntity.create(object.get(), level, blockPos, blockState);
        }

        @Override
        public BlockEntityType<T> get() {
            return object.get();
        }
    }
}
