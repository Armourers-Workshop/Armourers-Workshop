package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.registry.IEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBinder;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.TypedRegistry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EntityTypeBuilderImpl<T extends Entity> implements IEntityTypeBuilder<T> {

    private EntityType.Builder<T> builder;
    private IRegistryBinder<EntityType<T>> binder;

    public EntityTypeBuilderImpl(IEntityType.Serializer<T> serializer, MobCategory mobCategory) {
        this.builder = EntityType.Builder.of(serializer::create, mobCategory);
    }

    @Override
    public IEntityTypeBuilder<T> fixed(float f, float g) {
        this.builder = builder.sized(f, g);
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> noSummon() {
        this.builder = builder.noSummon();
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> noSave() {
        this.builder = builder.noSave();
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> fireImmune() {
        this.builder = builder.fireImmune();
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> specificSpawnBlocks(Block... blocks) {
        this.builder = builder.immuneTo(blocks);
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> spawnableFarFromPlayer() {
        this.builder = builder.canSpawnFarFromPlayer();
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> clientTrackingRange(int i) {
        this.builder = builder.clientTrackingRange(i);
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> updateInterval(int i) {
        this.builder = builder.updateInterval(i);
        return this;
    }

    @Override
    public IEntityTypeBuilder<T> bind(Supplier<AbstractEntityRendererProvider<T>> provider) {
        this.binder = () -> entityType -> {
            // here is safe call client registry.
            GameRenderer.registerEntityRendererFO(entityType, provider.get());
        };
        return this;
    }

    @Override
    public IRegistryHolder<IEntityType<T>> build(String name) {
        IRegistryHolder<EntityType<T>> object = AbstractForgeRegistries.ENTITY_TYPES.register(name, () -> builder.build(name));
        Proxy<T> proxy = new Proxy<>(object);
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT, IRegistryBinder.perform(binder, object));
        return TypedRegistry.Entry.of(object.getRegistryName(), () -> proxy);
    }

    public static class Proxy<T extends Entity> implements IEntityType<T> {

        private final IRegistryHolder<EntityType<T>> object;

        public Proxy(IRegistryHolder<EntityType<T>> object) {
            this.object = object;
        }

        @Override
        public T create(ServerLevel level, BlockPos pos, @Nullable ItemStack itemStack, MobSpawnType spawnType) {
            return object.get().create(level, pos, itemStack, spawnType);
        }

        @Override
        public EntityType<T> get() {
            return object.get();
        }
    }
}
