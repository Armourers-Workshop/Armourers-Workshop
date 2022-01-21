package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class EntityPropertyType<T extends INBTSerializable<CompoundNBT>> {

    private final static AtomicInteger COUNTER = new AtomicInteger();

    private final static HashMap<Class<?>, EntityPropertyType<?>> ALL_TYPES = new HashMap<>();
    private final static HashMap<Integer, EntityPropertyType<?>> ALL_TYPES_MAPPING = new HashMap<>();

    private final int id = COUNTER.incrementAndGet();

    private final Function<T, Entity> entity;
    private final Function<Entity, T> property;

    public EntityPropertyType(Function<T, Entity> entity, Function<Entity, T> property) {
        this.entity = entity;
        this.property = property;
    }

    public static EntityPropertyType<?> byId(int id) {
        return ALL_TYPES_MAPPING.get(id);
    }

    public static EntityPropertyType<?> byClass(Class<?> clazz) {
        return ALL_TYPES.get(clazz);
    }

    public static <T extends INBTSerializable<CompoundNBT>> void register(Class<T> clazz, Function<T, Entity> entity, Function<Entity, T> property) {
        EntityPropertyType<T> type = new EntityPropertyType<>(entity, property);
        ALL_TYPES.put(clazz, type);
        ALL_TYPES_MAPPING.put(type.getId(), type);
    }

    void apply(Entity entity, CompoundNBT nbt) {
        if (entity == null) {
            return;
        }
        T value = property.apply(entity);
        if (value == null) {
            return;
        }
        value.deserializeNBT(nbt);
    }

    @SuppressWarnings("unchecked")
    Entity getEntity(Object value) {
        return entity.apply((T) value);
    }

    public int getId() {
        return id;
    }

}
