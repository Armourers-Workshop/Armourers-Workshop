package moe.plushie.armourers_workshop.api.data;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;

public interface IGenericValue<S, T> {

    void apply(S source);

    void write(IFriendlyByteBuf buf);

    IGenericProperty<S, T> getProperty();

    T getValue();
}
