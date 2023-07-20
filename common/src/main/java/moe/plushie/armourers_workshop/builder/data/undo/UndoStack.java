package moe.plushie.armourers_workshop.builder.data.undo;

import moe.plushie.armourers_workshop.api.action.IUndoAction;
import moe.plushie.armourers_workshop.init.ModConfig;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.network.chat.Component;

import java.util.Stack;

public class UndoStack {

    private final Stack<IUndoAction> undoStack = new Stack<>();
    private final Stack<IUndoAction> redoStack = new Stack<>();

    public IUndoAction undo() throws Exception {
        if (undoStack.isEmpty()) {
            throw new CommandRuntimeException(Component.translatable("chat.armourers_workshop.undo.outOfUndos"));
        }
        IUndoAction changes = undoStack.peek();
        redoStack.push(changes.apply());
        undoStack.pop();
        return changes;
    }

    public IUndoAction redo() throws Exception {
        if (redoStack.isEmpty()) {
            throw new CommandRuntimeException(Component.translatable("chat.armourers_workshop.undo.outOfRedos"));
        }
        IUndoAction changes = redoStack.peek();
        undoStack.push(changes.apply());
        redoStack.pop();
        return changes;
    }

    public void push(IUndoAction action) {
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
