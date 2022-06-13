package moe.plushie.armourers_workshop.utils.undo.action;

import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import net.minecraft.command.CommandException;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;

public class UndoNamedGroupAction implements IUndoCommand {

    private final ITextComponent name;
    private final ArrayList<IUndoCommand> commands = new ArrayList<>();

    public UndoNamedGroupAction(ITextComponent name) {
        this.name = name;
    }

    public void push(IUndoCommand command) {
        commands.add(command);
    }

    @Override
    public void prepare() throws CommandException {
        for (IUndoCommand childCommand : commands) {
            childCommand.prepare();
        }
    }

    @Override
    public IUndoCommand apply() throws CommandException {
        // first prepare command to avoid backtracking, and then apply all child commands.
        prepare();
        UndoNamedGroupAction revertGroup = new UndoNamedGroupAction(name);
        for (IUndoCommand childCommand : commands) {
            IUndoCommand revertChildCommand = childCommand.apply();
            revertGroup.push(revertChildCommand);
        }
        return revertGroup;
    }

    @Override
    public ITextComponent name() {
        return name;
    }
}
