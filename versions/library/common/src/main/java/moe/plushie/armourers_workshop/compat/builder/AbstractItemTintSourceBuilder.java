package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class AbstractItemTintSourceBuilder<T extends ItemTintSource> {

    private final IDataMapCodec<T> codec;

    public AbstractItemTintSourceBuilder(IDataMapCodec<T> codec) {
        this.codec = codec;
    }

    public ItemTintSourceType<T> build(OpenResourceKey registryName) {
        return () -> codec;
    }
}
