package moe.plushie.armourers_workshop.core.network.packet;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.Hand;

public class UpdatePaintingToolPacket extends CustomPacket {

    private final Hand hand;
    private final ItemStack itemStack;

    public UpdatePaintingToolPacket(PacketBuffer buffer) {
        this.hand = buffer.readEnum(Hand.class);
        this.itemStack = buffer.readItem();
    }

    public UpdatePaintingToolPacket(Hand hand, ItemStack itemStack) {
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
        ItemStack oldItemStack = player.getItemInHand(hand);
        if (oldItemStack.getItem().equals(itemStack.getItem())) {
            ItemStack newItemStack = oldItemStack.copy();
            CompoundNBT nbt = itemStack.getTagElement("Options");
            if (nbt != null) {
                CompoundNBT itemTag = newItemStack.getOrCreateTag();
                itemTag.put("Options", nbt);
            } else {
                CompoundNBT itemTag = newItemStack.getTag();
                if (itemTag != null) {
                    itemTag.remove("Options");
                }
            }
            player.setItemInHand(hand, newItemStack);
        }
    }
}
