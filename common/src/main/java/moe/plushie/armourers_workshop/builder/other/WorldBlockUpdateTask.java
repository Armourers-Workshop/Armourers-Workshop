package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.common.IWorldUpdateTask;
import moe.plushie.armourers_workshop.utils.Constants;
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
    BlockPos pos;
    BlockState state;
    CompoundTag nbt;

    Predicate<BlockState> validator;
    Consumer<BlockState> modifier;

    public WorldBlockUpdateTask(Level level, BlockPos pos, BlockState state) {
        this(level, pos, state, null);
    }

    public WorldBlockUpdateTask(Level level, BlockPos pos, BlockState state, CompoundTag nbt) {
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.nbt = nbt;
    }

    public void setValidator(Predicate<BlockState> validator) {
        this.validator = validator;
    }

    public void setModifier(Consumer<BlockState> modifier) {
        this.modifier = modifier;
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
        BlockState targetState = level.getBlockState(pos);
        if (validator != null && !validator.test(targetState)) {
            return InteractionResult.PASS;
        }
        level.setBlock(pos, state, Constants.BlockFlags.DEFAULT);
        if (nbt != null) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity != null) {
                tileEntity.load(tileEntity.getBlockState(), nbt);
            }
        }
        if (modifier != null) {
            modifier.accept(state);
        }
        return InteractionResult.SUCCESS;
    }
}

