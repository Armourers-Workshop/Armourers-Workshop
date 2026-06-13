package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRenderer;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRendererType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class AbstractSpecialModelRendererBuilder<T extends SpecialModelRenderer<?>> {

    private final IDataMapCodec<T> codec;

    public AbstractSpecialModelRendererBuilder(IDataMapCodec<T> codec) {
        this.codec = codec;
    }

    public SpecialModelRendererType<T> build(OpenResourceKey registryName) {
        return () -> codec;
    }
}
