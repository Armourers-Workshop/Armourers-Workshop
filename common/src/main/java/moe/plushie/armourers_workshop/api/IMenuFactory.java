package moe.plushie.armourers_workshop.api;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

@FunctionalInterface
public interface IMenuFactory<T> {

    T create(int windowId, Inventory inventory, FriendlyByteBuf extraData);
}
