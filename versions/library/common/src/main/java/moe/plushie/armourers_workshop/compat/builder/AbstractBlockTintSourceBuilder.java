package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSource;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class AbstractBlockTintSourceBuilder<T extends BlockTintSource> {

    private final IDataMapCodec<T> codec;

    public AbstractBlockTintSourceBuilder(IDataMapCodec<T> codec) {
        this.codec = codec;
    }

    public BlockTintSourceType<T> build(OpenResourceKey registryName) {
        return () -> codec;
    }
}
