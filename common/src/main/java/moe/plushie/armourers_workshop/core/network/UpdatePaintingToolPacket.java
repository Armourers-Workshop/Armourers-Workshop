package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.other.network.IServerPacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class UpdatePaintingToolPacket extends CustomPacket {

    private final InteractionHand hand;
    private final ItemStack itemStack;

    public UpdatePaintingToolPacket(FriendlyByteBuf buffer) {
        this.hand = buffer.readEnum(InteractionHand.class);
        this.itemStack = buffer.readItem();
    }

    public UpdatePaintingToolPacket(InteractionHand hand, ItemStack itemStack) {
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
        ItemStack oldItemStack = player.getItemInHand(hand);
        if (oldItemStack.getItem().equals(itemStack.getItem())) {
            ItemStack newItemStack = oldItemStack.copy();
            CompoundTag nbt = itemStack.getTagElement("Options");
            if (nbt != null) {
                CompoundTag itemTag = newItemStack.getOrCreateTag();
                itemTag.put("Options", nbt);
            } else {
                CompoundTag itemTag = newItemStack.getTag();
                if (itemTag != null) {
                    itemTag.remove("Options");
                }
            }
            player.setItemInHand(hand, newItemStack);
        }
    }
}
