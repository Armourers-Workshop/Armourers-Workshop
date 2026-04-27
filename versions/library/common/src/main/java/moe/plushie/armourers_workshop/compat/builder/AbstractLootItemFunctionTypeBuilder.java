package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.common.ILootItemFunction;
import moe.plushie.armourers_workshop.api.common.ILootItemFunctionType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.compat.core.AbstractLootItemFunctionType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class AbstractLootItemFunctionTypeBuilder<T extends ILootItemFunction> {

    protected final IDataMapCodec<T> codec;

    public AbstractLootItemFunctionTypeBuilder(IDataMapCodec<T> codec) {
        this.codec = codec;
    }

    public ILootItemFunctionType<T> build(OpenResourceKey registryName) {
        return AbstractLootItemFunctionType.conditional(codec);
    }
}
