package moe.plushie.armourers_workshop.compat.core.data;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@Available("[1.21, 1.22)")
public class AbstractSavedDataType<T extends AbstractSavedData> {

    private final String id;
    private final SavedData.Factory<T> factory;

    public AbstractSavedDataType(String id, Supplier<T> factory, BiFunction<CompoundTag, HolderLookup.Provider, T> deserializer) {
        this.id = id;
        this.factory = new SavedData.Factory<>(factory, deserializer, DataFixTypes.SAVED_DATA_FORCED_CHUNKS);
    }

    public static <T extends AbstractSavedData> AbstractSavedDataType<T> create(Supplier<T> factory, String id) {
        return new AbstractSavedDataType<>(id, factory, (tag, provider1) -> {
            T value = factory.get();
            value.deserialize(new TagSerializer(tag, SerializationContext.from(provider1)));
            return value;
        });
    }

    public T computeIfAbsent(DimensionDataStorage storage) {
        return storage.computeIfAbsent(factory, id);
    }

    public T get(DimensionDataStorage storage) {
        return storage.get(factory, id);
    }
}
