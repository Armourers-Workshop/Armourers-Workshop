package extensions.net.minecraft.world.level.storage.DimensionDataStorage;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.core.AbstractSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.function.Supplier;

@Extension
@Available("[1.18, )")
public class DataProvider {

    public static <T extends AbstractSavedData> T computeIfAbsent(@This DimensionDataStorage storage, Supplier<T> provider, int flags, String name) {
        return storage.computeIfAbsent(tag -> {
           T value = provider.get();
           value.load(tag);
           return value;
        }, provider, name);
    }
}
