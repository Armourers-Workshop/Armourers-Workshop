package moe.plushie.armourers_workshop.builder.world;

import moe.plushie.armourers_workshop.utils.undo.UndoManager;
import moe.plushie.armourers_workshop.utils.undo.action.UndoNamedGroupAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;

public class SkinCubeApplier {

    private final World world;
    private final SkinCubeWrapper wrapper;
    private final ArrayList<SkinCubeChanges> changes = new ArrayList<>();

    public SkinCubeApplier(World world) {
        this.world = world;
        this.wrapper = new SkinCubeWrapper(world, changes::add);
    }

    public void submit(ITextComponent name, PlayerEntity player) {
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

    public World getWorld() {
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

