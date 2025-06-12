package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.action.IUserAction;
import moe.plushie.armourers_workshop.api.common.IWorldUpdateTask;
import moe.plushie.armourers_workshop.builder.data.undo.action.ActionRuntimeException;
import moe.plushie.armourers_workshop.core.data.paint.IBlockPaintable;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class CubeChanges implements IUserAction, IWorldUpdateTask {

    private final Level level;
    private final BlockPos blockPos;

    private BlockState blockState;
    private CompoundTag nbt;
    private Map<OpenDirection, SkinPaintColor> colors;

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

    public void setColor(OpenDirection dir, SkinPaintColor color) {
        if (this.colors == null) {
            this.colors = new HashMap<>();
        }
        this.colors.put(dir, color);
    }

    public void setColors(Map<OpenDirection, SkinPaintColor> colors) {
        this.colors = colors;
    }

    @Override
    public BlockPos blockPos() {
        return blockPos;
    }

    @Override
    public BlockState blockState() {
        return blockState;
    }

    private boolean isChangeNBT() {
        return nbt != null || colors != null;
    }

    @Override
    public void prepare() throws RuntimeException {
        // when change colors or nbt, required block entity
        if (!isChangeNBT()) {
            return;
        }
        // when change block state, the block entity will be created again.
        if (blockState != null) {
            return;
        }
        var blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity == null) {
            var value = String.format("x=%d, y=%d, z=%d", blockPos.getX(), blockPos.getY(), blockPos.getZ());
            throw new ActionRuntimeException(Component.translatable("chat.armourers_workshop.undo.missingBlock", value));
        }
    }

    @Override
    public IUserAction apply() throws RuntimeException {
        var isChangedNBT = false;
        var changes = new CubeChanges(level, blockPos);
        if (blockState != null) {
            changes.setBlockState(level.getBlockState(blockPos));
            isChangedNBT = true;
        }
        if (nbt != null) {
            isChangedNBT = true;
        }
        if (isChangedNBT) {
            Objects.flatMap(level.getBlockEntity(blockPos), blockEntity -> {
                var newTag = blockEntity.saveFullData(level.registryAccess());
                changes.setCompoundTag(newTag);
                return newTag;
            });
        } else if (colors != null) {
            var blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof IBlockPaintable target) {
                var oldValue = new HashMap<OpenDirection, SkinPaintColor>();
                for (var direction : colors.keySet()) {
                    var paintColor = target.getColor(direction);
                    if (paintColor == null) {
                        paintColor = SkinPaintColor.CLEAR;
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
    public Level level() {
        return level;
    }

    @Override
    public InteractionResult run(Level level) {
        if (!level.isLoaded(blockPos)) {
            return InteractionResult.PASS;
        }
        var changes = 0;
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
                blockEntity.loadFullData(nbt, level.registryAccess());
                changes += 1;
            }
        }
        if (colors != null) {
            if (blockEntity instanceof IBlockPaintable paintable) {
                paintable.setColors(colors);
                changes += 1;
            }
        }
        if (changes == 0) {
            return InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }
}
