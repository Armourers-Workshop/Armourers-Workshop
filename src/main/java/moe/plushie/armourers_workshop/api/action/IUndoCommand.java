package moe.plushie.armourers_workshop.api.action;

import net.minecraft.command.CommandException;
import net.minecraft.util.text.ITextComponent;

public interface IUndoCommand {

    default ITextComponent name() {
        return null;
    }

    default void prepare() throws CommandException {
    }

    IUndoCommand apply() throws CommandException;
}
