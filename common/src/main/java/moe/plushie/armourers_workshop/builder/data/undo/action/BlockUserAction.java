package moe.plushie.armourers_workshop.builder.data.undo.action;

import moe.plushie.armourers_workshop.api.action.IUserAction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockUserAction implements IUserAction {

    protected final Level level;
    protected final BlockPos blockPos;

    public BlockUserAction(Level level, BlockPos blockPos) {
        this.level = level;
        this.blockPos = blockPos;
    }

    @Override
    public void prepare() throws RuntimeException {
        var blockEntity = getBlockEntity();
        if (blockEntity == null) {
            var pos = String.format("x=%d, y=%d, z=%d", blockPos.getX(), blockPos.getY(), blockPos.getZ());
            throw new ActionRuntimeException(Component.translatable("chat.armourers_workshop.undo.missingBlock", pos));
        }
    }

    public BlockEntity getBlockEntity() {
        if (level != null) {
            return level.getBlockEntity(blockPos);
        }
        return null;
    }
}
