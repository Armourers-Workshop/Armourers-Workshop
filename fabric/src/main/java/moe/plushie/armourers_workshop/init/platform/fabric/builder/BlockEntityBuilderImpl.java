package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IBlockEntityKey;
import moe.plushie.armourers_workshop.api.common.IBlockEntitySupplier;
import moe.plushie.armourers_workshop.api.common.IRegistryBinder;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IBlockEntityBuilder;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricBlockEntity;
import moe.plushie.armourers_workshop.core.registry.Registries;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.ClientNativeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;
import java.util.function.Supplier;

public class BlockEntityBuilderImpl<T extends BlockEntity> implements IBlockEntityBuilder<T> {

    private IRegistryBinder<BlockEntityType<T>> binder;
    private final LinkedList<Supplier<Block>> blocks = new LinkedList<>();
    private final IBlockEntitySupplier<T> supplier;

    public BlockEntityBuilderImpl(IBlockEntitySupplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public IBlockEntityBuilder<T> of(Supplier<Block> block) {
        this.blocks.add(block);
        return this;
    }

    @Override
    public IBlockEntityBuilder<T> bind(Supplier<AbstractBlockEntityRendererProvider<T>> provider) {
        this.binder = () -> blockEntityType -> {
            // here is safe call client registry.
            ClientNativeManager.getProvider().entityRendererRegistry(it -> it.registerBlockEntity(blockEntityType, provider.get()));
        };
        return this;
    }

    @Override
    public IBlockEntityKey<T> build(String name) {
        IRegistryKey<BlockEntityType<T>> object = Registries.BLOCK_ENTITY_TYPE.register(name, () -> {
            Block[] blocks1 = blocks.stream().map(Supplier::get).toArray(Block[]::new);
            return AbstractFabricBlockEntity.createType(supplier, blocks1);
        });
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, IRegistryBinder.of(binder, object));
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
