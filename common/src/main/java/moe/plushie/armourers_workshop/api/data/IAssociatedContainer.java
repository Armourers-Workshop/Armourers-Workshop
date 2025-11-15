package moe.plushie.armourers_workshop.api.data;

import org.jetbrains.annotations.Nullable;

public interface IAssociatedContainer {

    <T> T getAssociatedObject(Key<T> key);

    <T> void setAssociatedObject(Key<T> key, T value);

    interface Key<T> {

        @Nullable
        default T defaultValue() {
            return null;
        }
    }
}
