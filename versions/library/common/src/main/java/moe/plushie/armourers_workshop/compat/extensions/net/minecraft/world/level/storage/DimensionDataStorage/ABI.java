package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.level.storage.DimensionDataStorage;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.data.AbstractPackResourcesImpl;
import moe.plushie.armourers_workshop.compat.core.data.AbstractSavedData;
import moe.plushie.armourers_workshop.compat.core.data.AbstractSavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.Nullable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, )")
@Extension
public class ABI {

    public static <T extends AbstractSavedData> T computeIfAbsent(@This DimensionDataStorage storage, AbstractSavedDataType<T> type) {
        return type.computeIfAbsent(storage);
    }

    @Nullable
    public static <T extends AbstractSavedData> T get(@This DimensionDataStorage storage, AbstractSavedDataType<T> type) {
        return type.get(storage);
    }
}
