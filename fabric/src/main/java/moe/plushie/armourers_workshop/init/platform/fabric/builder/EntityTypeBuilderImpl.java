package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IEntityRendererProvider;
import moe.plushie.armourers_workshop.api.common.IEntityTypeKey;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IEntityTypeBuilder;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricEntityRenderers;
import moe.plushie.armourers_workshop.core.registry.Registries;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.fabric.CommonNativeManagerImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.proxy.CommonProxyImpl;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

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
    public IEntityTypeKey<T> build(String name) {
        IRegistryKey<EntityType<T>> object = Registries.ENTITY_TYPE.register(name, () -> builder.build());
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, binder, object);
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
