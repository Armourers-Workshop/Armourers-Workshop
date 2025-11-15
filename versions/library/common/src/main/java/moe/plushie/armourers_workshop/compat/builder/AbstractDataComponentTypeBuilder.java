package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataComponentType;
import moe.plushie.armourers_workshop.compat.core.data.AbstractDataComponentType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

public class AbstractDataComponentTypeBuilder<T> {

    protected String tag;
    protected final IDataCodec<T> codec;

    public AbstractDataComponentTypeBuilder(IDataCodec<T> codec) {
        this.codec = codec;
    }

    public void tag(String tag) {
        this.tag = tag;
    }

    public IDataComponentType<T> build(OpenResourceLocation registryName) {
        return AbstractDataComponentType.create(tag, codec);
    }
}
