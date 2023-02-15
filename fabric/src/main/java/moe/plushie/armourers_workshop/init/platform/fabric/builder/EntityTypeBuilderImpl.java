package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IEntityRendererProvider;
import moe.plushie.armourers_workshop.api.common.IEntityTypeKey;
import moe.plushie.armourers_workshop.api.common.IRegistryBinder;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IEntityTypeBuilder;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricEntityRenderers;
import moe.plushie.armourers_workshop.core.registry.Registries;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.fabric.CommonNativeManagerImpl;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
            AbstractFabricEntityRenderers.registerEntity(entityType, provider.get());
        };
        return this;
    }

    @Override
    public IEntityTypeKey<T> build(String name) {
        IRegistryKey<EntityType<T>> object = Registries.ENTITY_TYPE.register(name, () -> builder.build());
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, IRegistryBinder.of(binder, object));
        return new IEntityTypeKey<T>() {
            @Override
            public T create(ServerLevel level, BlockPos pos, @Nullable CompoundTag tag, MobSpawnType spawnType) {
                return CommonNativeManagerImpl.INSTANCE.createEntity(object.get(), level, pos, tag, spawnType);
            }

            @Override
            public ResourceLocation getRegistryName() {
                return object.getRegistryName();
            }

            @Override
            public EntityType<T> get() {
                return object.get();
            }
        };
    }
}
