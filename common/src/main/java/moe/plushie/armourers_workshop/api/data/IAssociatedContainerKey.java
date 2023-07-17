package moe.plushie.armourers_workshop.api.data;

import org.jetbrains.annotations.Nullable;

public interface IAssociatedContainerKey<T> {

    Class<T> getType();

    @Nullable
    default T getDefaultValue() {
        return null;
    }
}
