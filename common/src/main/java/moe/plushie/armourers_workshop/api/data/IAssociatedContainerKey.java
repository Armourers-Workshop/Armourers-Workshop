package moe.plushie.armourers_workshop.api.data;

import org.jetbrains.annotations.Nullable;

public interface IAssociatedContainerKey<T> {

    Class<T> type();

    @Nullable
    default T defaultValue() {
        return null;
    }
}
