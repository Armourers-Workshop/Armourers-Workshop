package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.builder.data.undo.UndoManager;
import moe.plushie.armourers_workshop.builder.data.undo.action.NamedUserAction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class CubeChangesCollector {

    private final Level level;
    private final CubeWrapper wrapper;
    private final ArrayList<CubeChanges> allChanges = new ArrayList<>();

    public CubeChangesCollector(Level level) {
        this.level = level;
        this.wrapper = new CubeWrapper(level, allChanges::add);
    }

    public void submit(Component name, Player player) {
        // we need to ensure that all changes are commit to the changes queue.
        wrapper.setPos(null);
        if (allChanges.isEmpty()) {
            return;
        }
        // create an undo action and execute it immediately
        NamedUserAction group = new NamedUserAction(name);
        allChanges.forEach(group::push);
        UndoManager.of(player.getUUID()).push(group.apply());
    }

    public CubeWrapper getCube(BlockPos pos) {
        wrapper.setPos(pos);
        return wrapper;
    }

    public Level getLevel() {
        return level;
    }

    public int getTotal() {
        return allChanges.size();
    }
}

