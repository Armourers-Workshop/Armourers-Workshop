package moe.plushie.armourers_workshop.utils.undo;

import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.command.CommandException;

import java.util.Stack;

public class UndoStack {

    private final Stack<IUndoCommand> undoStack = new Stack<>();
    private final Stack<IUndoCommand> redoStack = new Stack<>();

    public IUndoCommand undo() throws CommandException {
        if (undoStack.isEmpty()) {
            throw new CommandException(TranslateUtils.title("chat.armourers_workshop.undo.outOfUndos"));
        }
        IUndoCommand changes = undoStack.peek();
        redoStack.push(changes.apply());
        undoStack.pop();
        return changes;
    }

    public IUndoCommand redo() throws CommandException {
        if (redoStack.isEmpty()) {
            throw new CommandException(TranslateUtils.title("chat.armourers_workshop.undo.outOfRedos"));
        }
        IUndoCommand changes = redoStack.peek();
        undoStack.push(changes.apply());
        redoStack.pop();
        return changes;
    }

    public void push(IUndoCommand command) {
        undoStack.push(command);
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
