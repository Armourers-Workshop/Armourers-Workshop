package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IBlockEntityKey;
import moe.plushie.armourers_workshop.api.common.IBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.api.common.IBlockEntitySupplier;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IBlockEntityBuilder;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricBlockEntity;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricBlockEntityRenderers;
import moe.plushie.armourers_workshop.core.registry.Registries;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.RendererManager;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockEntityBuilderImpl<T extends BlockEntity> implements IBlockEntityBuilder<T> {

    private Supplier<Consumer<BlockEntityType<T>>> binder;
    private final LinkedList<Supplier<Block>> blocks = new LinkedList<>();
    private final IBlockEntitySupplier<T> supplier;

    public BlockEntityBuilderImpl(IBlockEntitySupplier<T> supplier) {
        this.supplier = supplier;
    }

    private static <T extends BlockEntity, R> Function<R, BlockEntityRenderer<T>> bindEntityRenderer(Supplier<IBlockEntityRendererProvider<T>> provider) {
        return (context) -> provider.get().getBlockEntityRenderer(RendererManager.getBlockContext());
    }

    @Override
    public IBlockEntityBuilder<T> of(Supplier<Block> block) {
        this.blocks.add(block);
        return this;
    }

    @Override
    public IBlockEntityBuilder<T> bind(Supplier<IBlockEntityRendererProvider<T>> provider) {
        this.binder = () -> blockEntityType -> {
            // here is safe call client registry.
            AbstractFabricBlockEntityRenderers.register(blockEntityType, provider.get());
        };
        return this;
    }

    @Override
    public IBlockEntityKey<T> build(String name) {
        IRegistryKey<BlockEntityType<T>> object = Registries.BLOCK_ENTITY_TYPE.register(name, () -> {
            Block[] blocks1 = blocks.stream().map(Supplier::get).toArray(Block[]::new);
            return AbstractFabricBlockEntity.createType(supplier, blocks1);
        });
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, binder, object);
        return new IBlockEntityKey<T>() {

            @Override
            public T create(BlockGetter level, BlockPos blockPos, BlockState blockState) {
                return AbstractFabricBlockEntity.create(object.get(), level, blockPos, blockState);
            }

            @Override
            public BlockEntityType<T> get() {
                return object.get();
            }

            @Override
            public ResourceLocation getRegistryName() {
                return object.getRegistryName();
            }
        };
    }
}
