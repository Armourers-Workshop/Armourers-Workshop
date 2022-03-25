package moe.plushie.armourers_workshop.api.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;

public interface IPlayerDataSerializer<T> extends IDataSerializer<T> {

    void write(PacketBuffer buffer, PlayerEntity player, T value);

    T read(PacketBuffer buffer, PlayerEntity player);

    default void write(PacketBuffer buffer, T value) {
        write(buffer, null, value);
    }

    default T read(PacketBuffer buffer) {
        return read(buffer, null);
    }

    default T copy(T value) {
        return value;
    }
}
