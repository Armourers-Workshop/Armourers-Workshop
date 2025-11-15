package moe.plushie.armourers_workshop.api.common;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public interface ILootBuilder {

    <T> T getParameter(IContextKey<T> key);

    @Nullable
    <T> T getOptionalParameter(IContextKey<T> key);

    <T> ILootBuilder withParameter(IContextKey<T> key, T value);

    <T> ILootBuilder withOptionalParameter(IContextKey<T> key, @Nullable T value);

    ServerLevel level();
}
