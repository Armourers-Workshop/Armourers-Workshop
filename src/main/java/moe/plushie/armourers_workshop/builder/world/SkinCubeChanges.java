package moe.plushie.armourers_workshop.builder.world;

import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import moe.plushie.armourers_workshop.api.common.IWorldUpdateTask;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class SkinCubeChanges implements IUndoCommand, IWorldUpdateTask {

    private final World level;
    private final BlockPos pos;

    private BlockState state;
    private CompoundNBT nbt;
    private Map<Direction, IPaintColor> colors;

    public SkinCubeChanges(World level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public void setCompoundNBT(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    public void setColor(Direction dir, IPaintColor color) {
        if (this.colors == null) {
            this.colors = new HashMap<>();
        }
        this.colors.put(dir, color);
    }

    public void setColors(Map<Direction, IPaintColor> colors) {
        this.colors = colors;
    }


    public BlockPos getPos() {
        return pos;
    }

    private boolean isChangeNBT() {
        return nbt != null || colors != null;
    }

    @Override
    public void prepare() throws CommandException {
        // only change colors or nbt, required a block entity
        if (!isChangeNBT()) {
            return;
        }
        // when the block state is changed, the block entity will be created again.
        if (state != null) {
            return;
        }
        TileEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity == null) {
            String value = String.format("x=%d, y=%d, z=%d", pos.getX(), pos.getY(), pos.getZ());
            throw new CommandException(TranslateUtils.title("chat.armourers_workshop.undo.missingBlock", value));
        }
    }

    @Override
    public IUndoCommand apply() throws CommandException {
        boolean isChangedNBT = false;
        SkinCubeChanges changes = new SkinCubeChanges(level, pos);
        if (state != null) {
            changes.setState(level.getBlockState(pos));
            isChangedNBT = true;
        }
        if (nbt != null) {
            isChangedNBT = true;
        }
        if (isChangedNBT) {
            TileEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity != null) {
                changes.setCompoundNBT(tileEntity.serializeNBT());
            }
        } else if (colors != null){
            TileEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof IPaintable) {
                IPaintable target = (IPaintable) tileEntity;
                HashMap<Direction, IPaintColor> oldValue = new HashMap<>();
                for (Direction direction : colors.keySet()) {
                    IPaintColor paintColor = target.getColor(direction);
                    if (paintColor == null) {
                        paintColor = PaintColor.CLEAR;
                    }
                    oldValue.put(direction, paintColor);
                }
                changes.setColors(oldValue);
            }
        }
        WorldUpdater.getInstance().submit(this);
        return changes;
    }

    @Override
    public World getLevel() {
        return level;
    }

    @Override
    public ActionResultType run(World world) {
        if (!world.isLoaded(pos)) {
            return ActionResultType.PASS;
        }
        if (state != null) {
            world.setBlock(pos, state, Constants.BlockFlags.DEFAULT);
        }
        TileEntity tileEntity = null;
        if (isChangeNBT()) {
            tileEntity = world.getBlockEntity(pos);
        }
        if (nbt != null) {
            if (tileEntity != null) {
                tileEntity.deserializeNBT(nbt);
            }
        }
        if (colors != null) {
            if (tileEntity instanceof IPaintable) {
                ((IPaintable) tileEntity).setColors(colors);
            }
        }
        return ActionResultType.SUCCESS;
    }
}
