package moe.plushie.armourers_workshop.compatibility;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractSavedData extends SavedData {

    public static <T extends AbstractSavedData> T load(Function<CompoundTag, T> parser, Supplier<T> provider, DimensionDataStorage storage, String name) {
        return storage.computeIfAbsent(parser, provider, name);
    }
}
