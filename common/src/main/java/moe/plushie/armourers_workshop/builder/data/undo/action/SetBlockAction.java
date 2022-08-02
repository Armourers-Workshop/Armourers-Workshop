package moe.plushie.armourers_workshop.builder.data.undo.action;

import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SetBlockAction extends BlockUndoAction {

    private final BlockState newValue;
    private final CompoundTag newValueNBT;

    public SetBlockAction(Level level, BlockPos blockPos, BlockState newValue, CompoundTag newValueNBT) {
        super(level, blockPos);
        this.newValue = newValue;
        this.newValueNBT = newValueNBT;
    }

    @Override
    public void prepare() throws CommandRuntimeException {
        // no need any checks
    }

    @Override
    public IUndoCommand apply() {
        BlockState oldState = level.getBlockState(blockPos);
        CompoundTag oldNBT = null;
        BlockEntity oldTileEntity = level.getBlockEntity(blockPos);
        if (oldTileEntity != null) {
            oldNBT = oldTileEntity.save(new CompoundTag());
        }
        SetBlockAction oldChanges = new SetBlockAction(level, blockPos, oldState, oldNBT);
        level.setBlock(blockPos, newValue, Constants.BlockFlags.DEFAULT_AND_RERENDER);
        if (newValueNBT != null) {
            BlockEntity tileEntity = level.getBlockEntity(blockPos);
            if (tileEntity != null) {
                tileEntity.load(tileEntity.getBlockState(), newValueNBT);
            }
        }
        return oldChanges;
    }
}
