package moe.plushie.armourers_workshop.compat.client.block.tintsource;

import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class AbstractBlockTintSource {

    private final BlockTintSource[] sources;

    public AbstractBlockTintSource(Collection<? extends BlockTintSource> sources) {
        this.sources = sources.toArray(new BlockTintSource[0]);
    }

    public int calculate(BlockState state, @Nullable BlockGetter level, @Nullable BlockPos blockPos, int index) {
        if (index < sources.length) {
            var source = sources[index];
            if (source != null) {
                return source.calculate(state, level, blockPos);
            }
        }
        return -1;
    }
}

