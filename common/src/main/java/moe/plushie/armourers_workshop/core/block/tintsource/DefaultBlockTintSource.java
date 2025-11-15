package moe.plushie.armourers_workshop.core.block.tintsource;

import moe.plushie.armourers_workshop.api.client.IBlockTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.compat.core.block.AbstractBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DefaultBlockTintSource implements IBlockTintSource {

    public static final IDataMapCodec<DefaultBlockTintSource> MAP_CODEC = IDataMapCodec.create(it -> it.group(IDataCodec.INT.fieldOf("index").forGetter(DefaultBlockTintSource::layerIndex)).apply(it, DefaultBlockTintSource::new));

    private final int layerIndex;

    public DefaultBlockTintSource(int layerIndex) {
        this.layerIndex = layerIndex;
    }

    @Override
    public int calculate(BlockState blockState, @Nullable BlockAndTintGetter level, @Nullable BlockPos blockPos) {
        // AbstractBlock
        if (blockState.getBlock() instanceof AbstractBlock handler) {
            return handler.getModelTintColor(blockState, level, blockPos, layerIndex);
        }
        return 0xffffffff;
    }

    public int layerIndex() {
        return layerIndex;
    }

    @Override
    public IDataMapCodec<? extends DefaultBlockTintSource> type() {
        return MAP_CODEC;
    }
}
