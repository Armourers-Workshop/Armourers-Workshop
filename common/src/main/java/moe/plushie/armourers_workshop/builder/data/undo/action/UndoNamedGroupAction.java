package moe.plushie.armourers_workshop.builder.data.undo.action;

import moe.plushie.armourers_workshop.api.action.IUndoAction;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class UndoNamedGroupAction implements IUndoAction {

    private final Component name;
    private final ArrayList<IUndoAction> actions = new ArrayList<>();

    public UndoNamedGroupAction(Component name) {
        this.name = name;
    }

    public void push(IUndoAction command) {
        actions.add(command);
    }

    @Override
    public void prepare() throws CommandRuntimeException {
        for (IUndoAction childAction : actions) {
            childAction.prepare();
        }
    }

    @Override
    public IUndoAction apply() throws CommandRuntimeException {
        // first prepare command to avoid backtracking, and then apply all child commands.
        prepare();
        UndoNamedGroupAction revertGroup = new UndoNamedGroupAction(name);
        for (IUndoAction childAction : actions) {
            IUndoAction revertChildAction = childAction.apply();
            revertGroup.push(revertChildAction);
        }
        return revertGroup;
    }

    @Override
    public Component name() {
        return name;
    }
}
