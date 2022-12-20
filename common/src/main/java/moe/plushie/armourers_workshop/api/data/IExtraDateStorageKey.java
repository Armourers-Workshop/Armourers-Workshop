package moe.plushie.armourers_workshop.api.data;

import org.jetbrains.annotations.Nullable;

public interface IExtraDateStorageKey<T> {

    Class<T> getType();

    @Nullable
    default T getDefaultValue() {
        return null;
    }
}
