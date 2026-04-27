package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.client.ISpecialModelRenderer;
import moe.plushie.armourers_workshop.api.client.ISpecialModelRendererType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class AbstractSpecialModelRendererBuilder<T extends ISpecialModelRenderer<?>> {

    private final IDataMapCodec<T> codec;

    public AbstractSpecialModelRendererBuilder(IDataMapCodec<T> codec) {
        this.codec = codec;
    }

    public ISpecialModelRendererType<T> build(OpenResourceKey registryName) {
        return () -> codec;
    }
}
