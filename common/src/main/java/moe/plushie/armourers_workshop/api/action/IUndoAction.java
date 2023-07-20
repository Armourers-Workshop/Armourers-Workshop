package moe.plushie.armourers_workshop.api.action;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface IUndoAction {

    @Nullable
    default Component name() {
        return null;
    }

    default void prepare() throws CommandRuntimeException {
    }

    IUndoAction apply() throws CommandRuntimeException;
}
