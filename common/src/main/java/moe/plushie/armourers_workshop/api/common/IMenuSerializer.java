package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public interface IMenuSerializer<T> {

    void write(IFriendlyByteBuf buffer, Player player, T value);

    T read(IFriendlyByteBuf buffer, Player player);
}
