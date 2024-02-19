package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.registry.IEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBinder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProvider;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.TypedRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EntityTypeBuilderImpl<T extends Entity> implements IEntityTypeBuilder<T> {

    private FabricEntityTypeBuilder<T> builder;
    private IRegistryBinder<EntityType<T>> binder;

    public EntityTypeBuilderImpl(IEntityType.Serializer<T> serializer, MobCategory mobCategory) {
        this.builder = FabricEntityTypeBuilder.create(mobCategory, serializer::create);
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
    public IEntityTypeBuilder<T> bind(Supplier<AbstractEntityRendererProvider<T>> provider) {
        this.binder = () -> entityType -> {
            // here is safe call client registry.
            GameRenderer.registerEntityRendererFA(entityType, provider.get());
        };
        return this;
    }

    @Override
    public IRegistryKey<IEntityType<T>> build(String name) {
        IRegistryKey<EntityType<T>> object = Registry.registerEntityTypeFA(name, () -> builder.build());
        Proxy<T> proxy = new Proxy<>(object);
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT, IRegistryBinder.perform(binder, object));
        return TypedRegistry.Entry.ofValue(object.getRegistryName(), proxy);
    }

    public static class Proxy<T extends Entity> implements IEntityType<T> {

        private final IRegistryKey<EntityType<T>> object;

        public Proxy(IRegistryKey<EntityType<T>> object) {
            this.object = object;
        }

        @Override
        public T create(ServerLevel level, BlockPos pos, @Nullable CompoundTag tag, MobSpawnType spawnType) {
            return object.get().create(level, pos, tag, spawnType);
        }

        @Override
        public EntityType<T> get() {
            return object.get();
        }
    }
}
