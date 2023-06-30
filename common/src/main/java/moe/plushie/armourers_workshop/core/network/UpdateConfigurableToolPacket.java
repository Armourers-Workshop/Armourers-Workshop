package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.common.IConfigurableTool;
import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class UpdateConfigurableToolPacket extends CustomPacket {

    private final InteractionHand hand;
    private final ItemStack itemStack;

    public UpdateConfigurableToolPacket(FriendlyByteBuf buffer) {
        this.hand = buffer.readEnum(InteractionHand.class);
        this.itemStack = buffer.readItem();
    }

    public UpdateConfigurableToolPacket(InteractionHand hand, ItemStack itemStack) {
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
        IConfigurableTool configurableTool = ObjectUtils.safeCast(oldItemStack.getItem(), IConfigurableTool.class);
        if (configurableTool == null) {
            abort(player, "update", "tried change unsupported item type.");
            return;
        }
        if (!oldItemStack.getItem().equals(itemStack.getItem())) {
            abort(player, "update", "tried change item type.");
            return;
        }
        ItemStack newItemStack = oldItemStack.copy();
        copyTo(itemStack, newItemStack, IConfigurableToolProperty.OPTIONS_KEY);
        copyTo(itemStack, newItemStack, Constants.Key.COLOR);
        player.setItemInHand(hand, newItemStack);
    }

    private void copyTo(ItemStack fromItemStack, ItemStack toItemStack, String key) {
        CompoundTag nbt = fromItemStack.getTag();
        Tag value = null;
        if (nbt != null) {
            value = nbt.get(key);
        }
        if (value != null) {
            CompoundTag itemTag = toItemStack.getOrCreateTag();
            itemTag.put(key, value);
        } else {
            CompoundTag itemTag = toItemStack.getTag();
            if (itemTag != null) {
                itemTag.remove(key);
            }
        }
    }

    private void abort(Player player, String op, String reason) {
        String playerName = player.getScoreboardName();
        ModLog.info("abort {} request of the '{}', reason: '{}', from: '{}', to: '{}'", op, playerName, reason, player.getItemInHand(hand), itemStack);
    }
}
