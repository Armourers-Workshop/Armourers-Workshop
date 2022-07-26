package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.api.other.builder.IBlockEntityBuilder;
import moe.plushie.armourers_workshop.api.other.IRegistryObject;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockEntityBuilderImpl<T extends BlockEntity> implements IBlockEntityBuilder<T> {

    protected Supplier<Consumer<BlockEntityType<T>>> binder;
    protected final LinkedList<Supplier<Block>> blocks = new LinkedList<>();
    protected final Supplier<T> supplier;

    public BlockEntityBuilderImpl(Supplier<T> supplier) {
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
    public IRegistryObject<BlockEntityType<T>> build(String name) {
        IRegistryObject<BlockEntityType<T>> object = Registry.BLOCK_ENTITY_TYPE.register(name, () -> {
            Block[] blocks1 = blocks.stream().map(Supplier::get).toArray(Block[]::new);
            return BlockEntityType.Builder.of(supplier, blocks1).build(null);
        });
        EnvironmentExecutor.setupOn(EnvironmentType.CLIENT, binder, object);
        return object;
    }
}
