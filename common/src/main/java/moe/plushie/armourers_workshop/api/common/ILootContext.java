package moe.plushie.armourers_workshop.api.common;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface ILootContext {

    boolean hasParameter(IContextKey<?> key);

    <T> T getParameter(IContextKey<T> key);

    @Nullable
    <T> T getOptionalParameter(IContextKey<T> key);

    IRandomSource randomSource();
}
