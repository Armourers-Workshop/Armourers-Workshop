package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.other.network.IServerPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class UpdateColorPickerPacket extends CustomPacket {

    final InteractionHand hand;
    final ItemStack itemStack;

    public UpdateColorPickerPacket(FriendlyByteBuf buffer) {
        this.hand = buffer.readEnum(InteractionHand.class);
        this.itemStack = buffer.readItem();
    }

    public UpdateColorPickerPacket(InteractionHand hand, ItemStack itemStack) {
        this.hand = hand;
        this.itemStack = itemStack;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(hand);
        buffer.writeItem(itemStack);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // TODO: check player
        ItemStack holdItemStack = player.getItemInHand(hand);
        if (Objects.equals(holdItemStack.getItem(), itemStack.getItem())) {
            player.setItemInHand(hand, itemStack);
        }
    }
}
