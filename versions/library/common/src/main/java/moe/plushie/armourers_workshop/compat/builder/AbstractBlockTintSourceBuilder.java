package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.client.IBlockTintSource;
import moe.plushie.armourers_workshop.api.client.IBlockTintSourceType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

public class AbstractBlockTintSourceBuilder<T extends IBlockTintSource> {

    private final IDataMapCodec<T> codec;

    public AbstractBlockTintSourceBuilder(IDataMapCodec<T> codec) {
        this.codec = codec;
    }

    public IBlockTintSourceType<T> build(OpenResourceLocation registryName) {
        return () -> codec;
    }
}
