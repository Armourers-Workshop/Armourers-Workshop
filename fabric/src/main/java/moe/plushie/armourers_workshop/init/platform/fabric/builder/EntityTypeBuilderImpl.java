package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IEntityRendererProvider;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IEntityTypeBuilder;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricEntityRenderers;
import moe.plushie.armourers_workshop.core.registry.Registries;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityTypeBuilderImpl<T extends Entity> implements IEntityTypeBuilder<T> {

    private FabricEntityTypeBuilder<T> builder;
    private Supplier<Consumer<EntityType<T>>> binder;

    public EntityTypeBuilderImpl(EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory) {
        this.builder = FabricEntityTypeBuilder.create(mobCategory, entityFactory);
    }

    @Override
    public IEntityTypeBuilder<T> fixed(float f, float g) {
        this.builder = builder.dimensions(EntityDimensions.fixed(f, g));
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> noSummon() {
        this.builder = builder.disableSummon();
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> noSave() {
        this.builder = builder.disableSaving();
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> fireImmune() {
        this.builder = builder.fireImmune();
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> specificSpawnBlocks(Block... blocks) {
        this.builder = builder.specificSpawnBlocks(blocks);
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> spawnableFarFromPlayer() {
        this.builder = builder.spawnableFarFromPlayer();
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> clientTrackingRange(int i) {
        this.builder = builder.trackRangeBlocks(i);
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> updateInterval(int i) {
        this.builder = builder.trackedUpdateRate(i);
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> bind(Supplier<IEntityRendererProvider<T>> provider) {
        this.binder = () -> entityType -> {
            // here is safe call client registry.
            AbstractFabricEntityRenderers.register(entityType, provider.get());
        };
        return this;
    }

    @Override
    public IRegistryKey<EntityType<T>> build(String name) {
        IRegistryKey<EntityType<T>> object = Registries.ENTITY_TYPE.register(name, () -> builder.build());
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, binder, object);
        return object;
    }
}
