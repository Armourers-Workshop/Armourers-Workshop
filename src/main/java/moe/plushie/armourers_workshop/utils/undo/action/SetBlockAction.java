package moe.plushie.armourers_workshop.utils.undo.action;

import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class SetBlockAction extends BlockUndoAction {

    private final BlockState newValue;
    private final CompoundNBT newValueNBT;

    public SetBlockAction(IWorld world, BlockPos blockPos, BlockState newValue, CompoundNBT newValueNBT) {
        super(world, blockPos);
        this.newValue = newValue;
        this.newValueNBT = newValueNBT;
    }

    @Override
    public void prepare() throws CommandException {
        // no need any checks
    }

    @Override
    public IUndoCommand apply() {
        BlockState oldState = world.getBlockState(blockPos);
        CompoundNBT oldNBT = null;
        TileEntity oldTileEntity = world.getBlockEntity(blockPos);
        if (oldTileEntity != null) {
            oldNBT = oldTileEntity.serializeNBT();
        }
        SetBlockAction oldChanges = new SetBlockAction(world, blockPos, oldState, oldNBT);
        world.setBlock(blockPos, newValue, Constants.BlockFlags.DEFAULT_AND_RERENDER);
        if (newValueNBT != null) {
            TileEntity tileEntity = world.getBlockEntity(blockPos);
            if (tileEntity != null) {
                tileEntity.deserializeNBT(newValueNBT);
            }
        }
        return oldChanges;
    }
}
