package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBinder;
import moe.plushie.armourers_workshop.compat.forge.builder.AbstractForgeEntityTypeBuilder;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

@SuppressWarnings("Convert2MethodRef")
public class EntityTypeBuilderImpl<T extends Entity> implements IEntityTypeBuilder<T> {

    private IRegistryBinder<IEntityType<T>> binder;
    private final AbstractForgeEntityTypeBuilder<T> builder;

    public EntityTypeBuilderImpl(IEntityType.Serializer<T> serializer, MobCategory mobCategory) {
        this.builder = new AbstractForgeEntityTypeBuilder<>(serializer, mobCategory);
    }

    @Override
    public IEntityTypeBuilder<T> fixed(float f, float g) {
        this.builder.apply(it -> it.sized(f, g));
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> noSummon() {
        this.builder.apply(it -> it.noSummon());
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> noSave() {
        this.builder.apply(it -> it.noSave());
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> fireImmune() {
        this.builder.apply(it -> it.fireImmune());
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> specificSpawnBlocks(Block... blocks) {
        this.builder.apply(it -> it.immuneTo(blocks));
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> spawnableFarFromPlayer() {
        this.builder.apply(it -> it.canSpawnFarFromPlayer());
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> clientTrackingRange(int i) {
        this.builder.apply(it -> it.clientTrackingRange(i));
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> updateInterval(int i) {
        this.builder.apply(it -> it.updateInterval(i));
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> bind(Supplier<IEntityRenderer.Provider<T>> provider) {
        this.binder = () -> entityType -> {
            // here is safe call client registry.
            GameRenderer.registerEntityRendererFO(entityType, provider.get());
        };
        return this;
    }

    @Override
    public IRegistryHolder<IEntityType<T>> build(String name) {
        var entry = Registries.ENTITY_TYPES.register(name, builder::build);
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT, IRegistryBinder.perform(binder, entry));
        return entry;
    }
}
