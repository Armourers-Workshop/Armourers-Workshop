package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.builder.data.undo.UndoManager;
import moe.plushie.armourers_workshop.builder.data.undo.action.UndoNamedGroupAction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class SkinCubeApplier {

    private final Level world;
    private final SkinCubeWrapper wrapper;
    private final ArrayList<SkinCubeChanges> changes = new ArrayList<>();

    public SkinCubeApplier(Level world) {
        this.world = world;
        this.wrapper = new SkinCubeWrapper(world, changes::add);
    }

    public void submit(Component name, Player player) {
        // we need to ensure that all changes are commit to the changes queue.
        wrapper.setPos(null);
        if (changes.isEmpty()) {
            return;
        }
        // create an undo action and execute it immediately
        UndoNamedGroupAction group = new UndoNamedGroupAction(name);
        changes.forEach(group::push);
        UndoManager.of(player.getUUID()).push(group.apply());
    }

    public Level getWorld() {
        return world;
    }

    public int getChanges() {
        return changes.size();
    }

    public SkinCubeWrapper wrap(BlockPos pos) {
        wrapper.setPos(pos);
        return wrapper;
    }
}

