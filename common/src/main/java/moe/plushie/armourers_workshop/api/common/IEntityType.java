package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public interface IEntityType<T extends Entity> extends Supplier<EntityType<T>> {

    String getRegistryName();

    static <T extends Entity> IEntityType<T> of(String registryName) {
        return new IEntityType<T>() {

            boolean loaded;
            EntityType<T> entityType;

            @Override
            public String getRegistryName() {
                return registryName;
            }

            @Override
            public EntityType<T> get() {
                if (loaded) {
                    return entityType;
                }
                loaded = true;
                EntityType.byString(registryName).ifPresent(entityType1 -> {
                    // noinspection unchecked
                    entityType = (EntityType<T>) entityType1;
                });
                return entityType;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof IEntityType)) return false;
                IEntityType<?> that = (IEntityType<?>) o;
                return registryName.equals(that.getRegistryName());
            }

            @Override
            public int hashCode() {
                return registryName.hashCode();
            }
        };
    }
}
