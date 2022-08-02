package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import moe.plushie.armourers_workshop.api.common.IWorldUpdateTask;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class CubeChanges implements IUndoCommand, IWorldUpdateTask {

    private final Level level;
    private final BlockPos pos;

    private BlockState state;
    private CompoundTag nbt;
    private Map<Direction, IPaintColor> colors;

    public CubeChanges(Level level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public void setCompoundTag(CompoundTag nbt) {
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
    public void prepare() throws CommandRuntimeException {
        // only change colors or nbt, required a block entity
        if (!isChangeNBT()) {
            return;
        }
        // when the block state is changed, the block entity will be created again.
        if (state != null) {
            return;
        }
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity == null) {
            String value = String.format("x=%d, y=%d, z=%d", pos.getX(), pos.getY(), pos.getZ());
            throw new CommandRuntimeException(TranslateUtils.title("chat.armourers_workshop.undo.missingBlock", value));
        }
    }

    @Override
    public IUndoCommand apply() throws CommandRuntimeException {
        boolean isChangedNBT = false;
        CubeChanges changes = new CubeChanges(level, pos);
        if (state != null) {
            changes.setState(level.getBlockState(pos));
            isChangedNBT = true;
        }
        if (nbt != null) {
            isChangedNBT = true;
        }
        if (isChangedNBT) {
            ObjectUtils.ifPresent(level.getBlockEntity(pos), blockEntity -> {
                changes.setCompoundTag(blockEntity.save(new CompoundTag()));
            });
        } else if (colors != null) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
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
    public Level getLevel() {
        return level;
    }

    @Override
    public InteractionResult run(Level level) {
        if (!level.isLoaded(pos)) {
            return InteractionResult.PASS;
        }
        if (state != null) {
            level.setBlock(pos, state, Constants.BlockFlags.DEFAULT);
        }
        BlockEntity tileEntity = null;
        if (isChangeNBT()) {
            tileEntity = level.getBlockEntity(pos);
        }
        if (nbt != null) {
            if (tileEntity != null) {
                tileEntity.load(tileEntity.getBlockState(), nbt);
            }
        }
        if (colors != null) {
            if (tileEntity instanceof IPaintable) {
                ((IPaintable) tileEntity).setColors(colors);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
