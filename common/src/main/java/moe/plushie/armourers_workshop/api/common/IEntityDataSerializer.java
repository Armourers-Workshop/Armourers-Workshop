package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;

public interface IEntityDataSerializer<T> {

    T read(IFriendlyByteBuf buffer);

    void write(IFriendlyByteBuf buffer, T value);
}
