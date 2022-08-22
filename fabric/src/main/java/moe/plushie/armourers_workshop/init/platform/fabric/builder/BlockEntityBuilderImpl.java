package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IBlockEntityKey;
import moe.plushie.armourers_workshop.api.common.IBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.api.common.IBlockEntitySupplier;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IBlockEntityBuilder;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockEntityBuilderImpl<T extends BlockEntity> implements IBlockEntityBuilder<T> {

    private Supplier<Consumer<BlockEntityType<T>>> binder;
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
    public IBlockEntityBuilder<T> bind(Supplier<IBlockEntityRendererProvider<T>> provider) {
        this.binder = () -> blockEntityType -> {
            // here is safe call client registry.
            BlockEntityRendererRegistry.INSTANCE.register(blockEntityType, provider.get()::getBlockEntityRenderer);
            //BlockEntityRendererRegistry.INSTANCE.register(blockEntityType, provider.get()::getBlockEntityRenderer);
        };
        return this;
    }

    @Override
    public IBlockEntityKey<T> build(String name) {
        IRegistryKey<BlockEntityType<T>> object = Registry.BLOCK_ENTITY_TYPE.register(name, () -> {
            Block[] blocks1 = blocks.stream().map(Supplier::get).toArray(Block[]::new);
            BlockEntityType<?>[] entityTypes = {null};
            BlockEntityType<T> entityType = createBlockEntityType((blockPos, blockState) -> supplier.create(entityTypes[0], BlockPos.ZERO, null), blocks1);
            entityTypes[0] = entityType;
            return entityType;
        });
        EnvironmentExecutor.initOn(EnvironmentType.CLIENT, binder, object);
        return new IBlockEntityKey<T>() {

            @Override
            public T create(BlockGetter level, BlockPos blockPos, BlockState blockState) {
                //#if MC >= 11800
                //# return object.get().create(blockPos, blockState);
                //#else
                return object.get().create();
                //#endif
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

    private BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> consumer, Block... blocks) {
        //#if MC >= 11800
        //# return net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(consumer::apply, blocks1).build(null);
        //#else
        return BlockEntityType.Builder.of(() -> consumer.apply(BlockPos.ZERO, null), blocks).build(null);
        //#endif
    }
}
