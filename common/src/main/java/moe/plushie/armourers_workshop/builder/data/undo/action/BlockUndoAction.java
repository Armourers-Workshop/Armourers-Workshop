package moe.plushie.armourers_workshop.builder.data.undo.action;

import moe.plushie.armourers_workshop.api.action.IUndoAction;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockUndoAction implements IUndoAction {

    protected final Level level;
    protected final BlockPos blockPos;

    public BlockUndoAction(Level level, BlockPos blockPos) {
        this.level = level;
        this.blockPos = blockPos;
    }


    @Override
    public void prepare() throws CommandRuntimeException {
        BlockEntity blockEntity = getBlockEntity();
        if (blockEntity == null) {
            String pos = String.format("x=%d, y=%d, z=%d", blockPos.getX(), blockPos.getY(), blockPos.getZ());
            throw new CommandRuntimeException(Component.translatable("chat.armourers_workshop.undo.missingBlock", pos));
        }
    }

    public BlockEntity getBlockEntity() {
        if (level != null) {
            return level.getBlockEntity(blockPos);
        }
        return null;
    }
}
