package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public interface IPlayerDataSerializer<T> extends IEntitySerializer<T> {

    void write(IFriendlyByteBuf buffer, Player player, T value);

    T read(IFriendlyByteBuf buffer, Player player);

    default void write(IFriendlyByteBuf buffer, T value) {
        write(buffer, null, value);
    }

    default T read(IFriendlyByteBuf buffer) {
        return read(buffer, null);
    }

    default T copy(T value) {
        return value;
    }
}
