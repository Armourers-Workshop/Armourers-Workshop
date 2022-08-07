package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IBlockEntityBuilder;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockEntityBuilderImpl<T extends BlockEntity> implements IBlockEntityBuilder<T> {

    private Supplier<Consumer<BlockEntityType<T>>> binder;
    private final LinkedList<Supplier<Block>> blocks = new LinkedList<>();
    private final Function<BlockEntityType<?>, T> supplier;

    public BlockEntityBuilderImpl(Function<BlockEntityType<?>, T> supplier) {
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
        };
        return this;
    }

    @Override
    public IRegistryKey<BlockEntityType<T>> build(String name) {
        IRegistryKey<BlockEntityType<T>> object = Registry.BLOCK_ENTITY_TYPE.register(name, () -> {
            Block[] blocks1 = blocks.stream().map(Supplier::get).toArray(Block[]::new);
            BlockEntityType<?>[] entityTypes = {null};
            BlockEntityType<T> entityType = BlockEntityType.Builder.of(() -> supplier.apply(entityTypes[0]), blocks1).build(null);
            entityTypes[0] = entityType;
            return entityType;
        });
        EnvironmentExecutor.initOn(EnvironmentType.CLIENT, binder, object);
        return object;
    }
}
