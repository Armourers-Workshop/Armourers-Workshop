package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.level.storage.DimensionDataStorage;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.core.AbstractSavedData;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.21, )")
public class DataProvider {

    public static <T extends AbstractSavedData> T computeIfAbsent(@This DimensionDataStorage storage, Supplier<T> provider, int flags, String name) {
        BiFunction<CompoundTag, HolderLookup.Provider, T> deserializer = (tag, provider1) -> {
            T value = provider.get();
            value.deserialize(AbstractDataSerializer.wrap(tag, provider1));
            return value;
        };
        return storage.computeIfAbsent(new SavedData.Factory<>(provider, deserializer, DataFixTypes.SAVED_DATA_FORCED_CHUNKS), name);
    }
}
