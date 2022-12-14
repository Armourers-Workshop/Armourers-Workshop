package moe.plushie.armourers_workshop.api.common;

import net.minecraft.network.FriendlyByteBuf;

public interface IEntitySerializer<T> {

    T read(FriendlyByteBuf buffer);

    void write(FriendlyByteBuf buffer, T descriptor);
}
