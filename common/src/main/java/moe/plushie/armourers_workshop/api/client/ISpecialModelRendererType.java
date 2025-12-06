package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public interface ISpecialModelRendererType<T extends ISpecialModelRenderer<?>> {

    IDataMapCodec<T> codec();
}
