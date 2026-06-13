package moe.plushie.armourers_workshop.core.client.special;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public interface SpecialModelRendererType<T extends SpecialModelRenderer<?>> {

    IDataMapCodec<T> codec();
}
