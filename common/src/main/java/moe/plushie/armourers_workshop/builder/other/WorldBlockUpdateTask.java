package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.common.IWorldUpdateTask;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class WorldBlockUpdateTask implements IWorldUpdateTask {

    Level level;
    BlockPos blockPos;
    BlockState blockState;
    CompoundTag nbt;

    Predicate<BlockState> validator;
    Consumer<BlockState> modifier;

    private int blockFlags = Constants.BlockFlags.DEFAULT;

    public WorldBlockUpdateTask(Level level, BlockPos blockPos, BlockState blockState) {
        this(level, blockPos, blockState, null);
    }

    public WorldBlockUpdateTask(Level level, BlockPos blockPos, BlockState blockState, CompoundTag nbt) {
        this.level = level;
        this.blockPos = blockPos;
        this.blockState = blockState;
        this.nbt = nbt;
    }

    public void setValidator(Predicate<BlockState> validator) {
        this.validator = validator;
    }

    public void setModifier(Consumer<BlockState> modifier) {
        this.modifier = modifier;
    }

    public void setBlockFlags(int blockFlags) {
        this.blockFlags = blockFlags;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public BlockState getBlockState() {
        return blockState;
    }

    @Override
    public InteractionResult run(Level level) {
        if (!level.isLoaded(blockPos)) {
            return InteractionResult.PASS;
        }
        if (validator != null && !validator.test(level.getBlockState(blockPos))) {
            return InteractionResult.PASS;
        }
        level.setBlock(blockPos, blockState, blockFlags);
        if (nbt != null) {
            BlockEntity tileEntity = level.getBlockEntity(blockPos);
            DataSerializers.loadBlockTag(tileEntity, nbt);
        }
        if (modifier != null) {
            modifier.accept(blockState);
        }
        return InteractionResult.SUCCESS;
    }
}

