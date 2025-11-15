package moe.plushie.armourers_workshop.compat.client.block.tintsource;

import moe.plushie.armourers_workshop.api.client.IBlockTintSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class AbstractBlockTintSource {

    private final IBlockTintSource[] sources;

    public AbstractBlockTintSource(Collection<? extends IBlockTintSource> sources) {
        this.sources = sources.toArray(new IBlockTintSource[0]);
    }

    public int calculate(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos blockPos, int index) {
        if (index < sources.length) {
            var source = sources[index];
            if (source != null) {
                return source.calculate(state, level, blockPos);
            }
        }
        return -1;
    }
}

