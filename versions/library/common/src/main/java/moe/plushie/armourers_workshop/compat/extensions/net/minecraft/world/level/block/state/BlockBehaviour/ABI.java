package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.level.block.state.BlockBehaviour;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.22)")
@Extension
public class ABI {

    @Extension
    public static class BlockStateBase {

        public static int getAnalogOutputSignal(@This BlockBehaviour.BlockStateBase behaviour, Level level, BlockPos blockPos, Direction dir) {
            return behaviour.getAnalogOutputSignal(level, blockPos);
        }
    }
}
