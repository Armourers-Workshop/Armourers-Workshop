package moe.plushie.armourers_workshop.utils.undo;

import java.util.HashMap;
import java.util.UUID;

public class UndoManager {

    private static final HashMap<UUID, UndoStack> stacks = new HashMap<>();

    public static UndoStack of(UUID player) {
        return stacks.computeIfAbsent(player, i -> new UndoStack());
    }

}
