package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.client.IBlockEntityRenderer;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBinder;
import moe.plushie.armourers_workshop.compat.forge.builder.AbstractForgeBlockEntityTypeBuilder;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Supplier;

public class BlockEntityTypeBuilderImpl<T extends BlockEntity> implements IBlockEntityTypeBuilder<T> {

    private IRegistryBinder<IBlockEntityType<T>> binder;
    private final AbstractForgeBlockEntityTypeBuilder<T> builder;

    public BlockEntityTypeBuilderImpl(IBlockEntityType.Serializer<T> serializer) {
        this.builder = new AbstractForgeBlockEntityTypeBuilder<>(serializer);
    }

    @Override
    public IBlockEntityTypeBuilder<T> of(Supplier<Block> block) {
        this.builder.add(block);
        return this;
    }

    @Override
    public IBlockEntityTypeBuilder<T> bind(Supplier<IBlockEntityRenderer.Provider<T>> provider) {
        this.binder = () -> blockEntityType -> {
            // here is safe call client registry.
            GameRenderer.registerBlockEntityRendererFO(blockEntityType, provider.get());
        };
        return this;
    }

    @Override
    public IRegistryHolder<IBlockEntityType<T>> build(String name) {
        var entry = Registries.BLOCK_ENTITY_TYPES.register(name, builder::build);
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT, IRegistryBinder.perform(binder, entry));
        return entry;
    }
}
