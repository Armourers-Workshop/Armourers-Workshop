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
    private final BlockPos blockPos;

    private BlockState blockState;
    private CompoundTag nbt;
    private Map<Direction, IPaintColor> colors;

    public CubeChanges(Level level, BlockPos blockPos) {
        this.level = level;
        this.blockPos = blockPos;
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
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

    @Override
    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public BlockState getBlockState() {
        return blockState;
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
        if (blockState != null) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity == null) {
            String value = String.format("x=%d, y=%d, z=%d", blockPos.getX(), blockPos.getY(), blockPos.getZ());
            throw new CommandRuntimeException(TranslateUtils.title("chat.armourers_workshop.undo.missingBlock", value));
        }
    }

    @Override
    public IUndoCommand apply() throws CommandRuntimeException {
        boolean isChangedNBT = false;
        CubeChanges changes = new CubeChanges(level, blockPos);
        if (blockState != null) {
            changes.setBlockState(level.getBlockState(blockPos));
            isChangedNBT = true;
        }
        if (nbt != null) {
            isChangedNBT = true;
        }
        if (isChangedNBT) {
            ObjectUtils.ifPresent(level.getBlockEntity(blockPos), blockEntity -> {
                CompoundTag newTag = blockEntity.saveWithFullMetadata();
                changes.setCompoundTag(newTag);
            });
        } else if (colors != null) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof IPaintable) {
                IPaintable target = (IPaintable) blockEntity;
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
        if (!level.isLoaded(blockPos)) {
            return InteractionResult.PASS;
        }
        int changes = 0;
        if (blockState != null && !blockState.equals(level.getBlockState(blockPos))) {
            level.setBlock(blockPos, blockState, Constants.BlockFlags.DEFAULT);
            changes += 1;
        }
        BlockEntity blockEntity = null;
        if (isChangeNBT()) {
            blockEntity = level.getBlockEntity(blockPos);
        }
        if (nbt != null) {
            if (blockEntity != null) {
                blockEntity.load(nbt);
                changes += 1;
            }
        }
        if (colors != null) {
            if (blockEntity instanceof IPaintable) {
                ((IPaintable) blockEntity).setColors(colors);
                changes += 1;
            }
        }
        if (changes == 0) {
            return InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }
}
