package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.client.IItemTintSourceType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class AbstractItemTintSourceBuilder<T extends IItemTintSource> {

    private final IDataMapCodec<T> codec;

    public AbstractItemTintSourceBuilder(IDataMapCodec<T> codec) {
        this.codec = codec;
    }

    public IItemTintSourceType<T> build(OpenResourceKey registryName) {
        return () -> codec;
    }
}
