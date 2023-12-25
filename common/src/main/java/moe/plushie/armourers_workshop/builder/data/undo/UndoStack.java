package moe.plushie.armourers_workshop.builder.data.undo;

import moe.plushie.armourers_workshop.api.action.IUserAction;
import moe.plushie.armourers_workshop.init.ModConfig;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.network.chat.Component;

import java.util.Stack;

public class UndoStack {

    private final Stack<IUserAction> undoStack = new Stack<>();
    private final Stack<IUserAction> redoStack = new Stack<>();

    public IUserAction undo() throws Exception {
        if (undoStack.isEmpty()) {
            throw new CommandRuntimeException(Component.translatable("chat.armourers_workshop.undo.outOfUndos"));
        }
        IUserAction changes = undoStack.peek();
        redoStack.push(changes.apply());
        undoStack.pop();
        return changes;
    }

    public IUserAction redo() throws Exception {
        if (redoStack.isEmpty()) {
            throw new CommandRuntimeException(Component.translatable("chat.armourers_workshop.undo.outOfRedos"));
        }
        IUserAction changes = redoStack.peek();
        undoStack.push(changes.apply());
        redoStack.pop();
        return changes;
    }

    public void push(IUserAction action) {
        undoStack.push(action);
        redoStack.clear();
        // when the maximum undo count is exceeded, the old undo command must clear.
        while (!undoStack.isEmpty() && undoStack.size() > ModConfig.Common.maxUndos) {
            undoStack.removeElementAt(0);
        }
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
