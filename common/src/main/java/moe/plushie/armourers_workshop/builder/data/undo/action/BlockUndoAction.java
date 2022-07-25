package moe.plushie.armourers_workshop.builder.data.undo.action;

import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockUndoAction implements IUndoCommand {

    protected final Level world;
    protected final BlockPos blockPos;

    public BlockUndoAction(Level world, BlockPos blockPos) {
        this.world = world;
        this.blockPos = blockPos;
    }


    @Override
    public void prepare() throws CommandRuntimeException {
        BlockEntity tileEntity = getTileEntity();
        if (tileEntity == null) {
            String pos = String.format("x=%d, y=%d, z=%d", blockPos.getX(), blockPos.getY(), blockPos.getZ());
            throw new CommandRuntimeException(TranslateUtils.title("chat.armourers_workshop.undo.missingBlock", pos));
        }
    }

    public BlockEntity getTileEntity() {
        if (world != null) {
            return world.getBlockEntity(blockPos);
        }
        return null;
    }
}
