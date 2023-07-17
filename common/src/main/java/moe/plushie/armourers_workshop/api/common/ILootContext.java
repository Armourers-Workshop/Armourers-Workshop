package moe.plushie.armourers_workshop.api.common;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface ILootContext {

    <T> T getParameter(ILootContextParam<T> param);

    @Nullable <T> T getOptionalParameter(ILootContextParam<T> param);
}
