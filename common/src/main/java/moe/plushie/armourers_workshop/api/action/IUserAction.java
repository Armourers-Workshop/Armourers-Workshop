package moe.plushie.armourers_workshop.api.action;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface IUserAction {

    @Nullable
    default Component name() {
        return null;
    }

    default void prepare() throws RuntimeException {
    }

    IUserAction apply() throws RuntimeException;
}
