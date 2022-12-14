package moe.plushie.armourers_workshop.api.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.player.Player;

public interface IPlayerDataSerializer<T> extends EntityDataSerializer<T>, IEntitySerializer<T> {

    void write(FriendlyByteBuf buffer, Player player, T value);

    T read(FriendlyByteBuf buffer, Player player);

    default void write(FriendlyByteBuf buffer, T value) {
        write(buffer, null, value);
    }

    default T read(FriendlyByteBuf buffer) {
        return read(buffer, null);
    }

    default T copy(T value) {
        return value;
    }
}
