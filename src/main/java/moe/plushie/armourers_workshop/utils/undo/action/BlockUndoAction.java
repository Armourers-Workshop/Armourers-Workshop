package moe.plushie.armourers_workshop.utils.undo.action;

import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.command.CommandException;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public abstract class BlockUndoAction implements IUndoCommand {

    protected final IWorld world;
    protected final BlockPos blockPos;

    public BlockUndoAction(IWorld world, BlockPos blockPos) {
        this.world = world;
        this.blockPos = blockPos;
    }


    @Override
    public void prepare() throws CommandException {
        TileEntity tileEntity = getTileEntity();
        if (tileEntity == null) {
            String pos = String.format("x=%d, y=%d, z=%d", blockPos.getX(), blockPos.getY(), blockPos.getZ());
            throw new CommandException(TranslateUtils.title("chat.armourers_workshop.undo.missingBlock", pos));
        }
    }

    public TileEntity getTileEntity() {
        if (world != null) {
            return world.getBlockEntity(blockPos);
        }
        return null;
    }
}
