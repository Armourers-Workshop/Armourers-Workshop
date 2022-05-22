package moe.plushie.armourers_workshop.builder.world;

import moe.plushie.armourers_workshop.api.common.IWorldUpdateTask;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WorldBlockUpdateTask implements IWorldUpdateTask {

    World world;
    BlockPos pos;
    BlockState state;

    Predicate<BlockState> validator;
    Consumer<BlockState> modifier;

    public WorldBlockUpdateTask(World world, BlockPos pos, BlockState state) {
        this.world = world;
        this.pos = pos;
        this.state = state;
    }

    public void setValidator(Predicate<BlockState> validator) {
        this.validator = validator;
    }

    public void setModifier(Consumer<BlockState> modifier) {
        this.modifier = modifier;
    }

    @Override
    public World getLevel() {
        return world;
    }

    @Override
    public ActionResultType run(World world) {
        if (!world.isLoaded(pos)) {
            return ActionResultType.PASS;
        }
        BlockState targetState = world.getBlockState(pos);
        if (validator != null && !validator.test(targetState)) {
            return ActionResultType.PASS;
        }
        world.setBlock(pos, state, Constants.BlockFlags.DEFAULT);
        if (modifier != null) {
            modifier.accept(state);
        }
        return ActionResultType.SUCCESS;
    }
}
