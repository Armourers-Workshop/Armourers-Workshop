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

    Level world;
    BlockPos pos;
    BlockState state;
    CompoundTag nbt;

    Predicate<BlockState> validator;
    Consumer<BlockState> modifier;

    public WorldBlockUpdateTask(Level world, BlockPos pos, BlockState state) {
        this(world, pos, state, null);
    }

    public WorldBlockUpdateTask(Level world, BlockPos pos, BlockState state, CompoundTag nbt) {
        this.world = world;
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
        return world;
    }

    @Override
    public InteractionResult run(Level world) {
        if (!world.isLoaded(pos)) {
            return InteractionResult.PASS;
        }
        BlockState targetState = world.getBlockState(pos);
        if (validator != null && !validator.test(targetState)) {
            return InteractionResult.PASS;
        }
        world.setBlock(pos, state, Constants.BlockFlags.DEFAULT);
        if (nbt != null) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
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

