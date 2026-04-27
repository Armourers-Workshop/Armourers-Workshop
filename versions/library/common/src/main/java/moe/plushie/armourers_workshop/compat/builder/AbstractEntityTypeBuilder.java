package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IEntitySpawnReason;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.core.utils.LazyValue;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.core.utils.TypedHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

@Available("[16, )")
public class AbstractEntityTypeBuilder<T extends Entity> {

    protected final IEntityType.Serializer<T> serializer;
    protected final MobCategory category;

    protected final ArrayList<Function<EntityType.Builder<T>, EntityType.Builder<T>>> updaters = new ArrayList<>();

    public AbstractEntityTypeBuilder(IEntityType.Serializer<T> serializer, MobCategory category) {
        this.serializer = serializer;
        this.category = category;
    }

    public static <T extends Entity> TypedHolder<EntityType<T>> lazy(String id) {
        // noinspection unchecked
        var value = LazyValue.of(() -> (EntityType<T>) EntityType.byString(id).orElse(null));
        var registryName = OpenResourceKey.parse(id);
        return TypedHolder.of(registryName, value);
    }

    public void apply(Function<EntityType.Builder<T>, EntityType.Builder<T>> updater) {
        this.updaters.add(updater);
    }

    public IEntityType<T> build(OpenResourceKey registryName) {
        return new Proxy<>(create(registryName));
    }

    protected EntityType<T> create(OpenResourceKey registryName) {
        var builder = EntityType.Builder.of(serializer::create, category);
        for (var updater : updaters) {
            builder = updater.apply(builder);
        }
        return builder.build(registryName);
    }

    private static class Proxy<T extends Entity> implements IEntityType<T> {

        private final EntityType<T> entityType;

        public Proxy(EntityType<T> entityType) {
            this.entityType = entityType;
        }

        @Override
        public T create(ServerLevel level, BlockPos pos, @Nullable ItemStack itemStack, IEntitySpawnReason reason) {
            return entityType.create(level, pos, itemStack, reason);
        }

        @Override
        public EntityType<T> get() {
            return entityType;
        }
    }
}
