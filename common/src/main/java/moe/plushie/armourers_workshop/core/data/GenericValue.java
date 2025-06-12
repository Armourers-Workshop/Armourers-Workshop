package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;

public abstract class GenericValue<S, T> {

    public abstract void apply(S source);

    public abstract void write(IFriendlyByteBuf buf);

    public abstract GenericProperty<S, T> property();

    public abstract T value();
}
