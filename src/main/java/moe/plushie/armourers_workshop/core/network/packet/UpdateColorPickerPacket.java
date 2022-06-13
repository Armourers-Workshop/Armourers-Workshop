package moe.plushie.armourers_workshop.core.network.packet;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.Hand;

import java.util.Objects;

public class UpdateColorPickerPacket extends CustomPacket {

    final Hand hand;
    final ItemStack itemStack;

    public UpdateColorPickerPacket(PacketBuffer buffer) {
        this.hand = buffer.readEnum(Hand.class);
        this.itemStack = buffer.readItem();
    }

    public UpdateColorPickerPacket(Hand hand, ItemStack itemStack) {
        this.hand = hand;
        this.itemStack = itemStack;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeEnum(hand);
        buffer.writeItem(itemStack);
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        // TODO: check player
        ItemStack holdItemStack = player.getItemInHand(hand);
        if (Objects.equals(holdItemStack.getItem(), itemStack.getItem())) {
            player.setItemInHand(hand, itemStack);
        }
    }
}
