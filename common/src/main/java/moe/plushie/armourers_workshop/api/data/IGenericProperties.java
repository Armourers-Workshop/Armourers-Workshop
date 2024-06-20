package moe.plushie.armourers_workshop.api.data;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;

public interface IGenericProperties<S> {

    IGenericValue<S, ?> read(IFriendlyByteBuf buf);
}
