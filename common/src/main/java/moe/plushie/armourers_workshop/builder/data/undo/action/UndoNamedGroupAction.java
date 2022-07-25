package moe.plushie.armourers_workshop.builder.data.undo.action;

import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class UndoNamedGroupAction implements IUndoCommand {

    private final Component name;
    private final ArrayList<IUndoCommand> commands = new ArrayList<>();

    public UndoNamedGroupAction(Component name) {
        this.name = name;
    }

    public void push(IUndoCommand command) {
        commands.add(command);
    }

    @Override
    public void prepare() throws CommandRuntimeException {
        for (IUndoCommand childCommand : commands) {
            childCommand.prepare();
        }
    }

    @Override
    public IUndoCommand apply() throws CommandRuntimeException {
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
    public Component name() {
        return name;
    }
}
