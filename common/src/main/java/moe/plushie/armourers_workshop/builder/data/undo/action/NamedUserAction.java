package moe.plushie.armourers_workshop.builder.data.undo.action;

import moe.plushie.armourers_workshop.api.action.IUserAction;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class NamedUserAction implements IUserAction {

    private final Component name;
    private final ArrayList<IUserAction> actions = new ArrayList<>();

    public NamedUserAction(Component name) {
        this.name = name;
    }

    public void push(IUserAction command) {
        actions.add(command);
    }

    @Override
    public void prepare() throws CommandRuntimeException {
        for (IUserAction childAction : actions) {
            childAction.prepare();
        }
    }

    @Override
    public IUserAction apply() throws CommandRuntimeException {
        // first prepare command to avoid backtracking, and then apply all child commands.
        prepare();
        NamedUserAction revertGroup = new NamedUserAction(name);
        for (IUserAction childAction : actions) {
            IUserAction revertChildAction = childAction.apply();
            revertGroup.push(revertChildAction);
        }
        return revertGroup;
    }

    @Override
    public Component name() {
        return name;
    }
}
