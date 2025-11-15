package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.common.IConfigurableTool;
import moe.plushie.armourers_workshop.api.core.IDataComponentType;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class UpdateConfigurableToolPacket extends CustomPacket {

    private final OpenInteractionHand hand;
    private final ItemStack itemStack;

    public UpdateConfigurableToolPacket(IFriendlyByteBuf buffer) {
        this.hand = buffer.readEnum(OpenInteractionHand.class);
        this.itemStack = buffer.readItem();
    }

    public UpdateConfigurableToolPacket(OpenInteractionHand hand, ItemStack itemStack) {
        this.hand = hand;
        this.itemStack = itemStack;
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeEnum(hand);
        buffer.writeItem(itemStack);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // TODO: check player
        var oldItemStack = player.getItemInHand(hand);
        if (!(oldItemStack.getItem() instanceof IConfigurableTool)) {
            abort(player, "update", "tried change unsupported item type.");
            return;
        }
        if (!oldItemStack.getItem().equals(itemStack.getItem())) {
            abort(player, "update", "tried change item type.");
            return;
        }
        var newItemStack = oldItemStack.copy();
        copyTo(itemStack, newItemStack, ModDataComponents.TOOL_OPTIONS.get());
        copyTo(itemStack, newItemStack, ModDataComponents.TOOL_COLOR.get());
        player.setItemInHand(hand, newItemStack);
    }

    private <T> void copyTo(ItemStack fromItemStack, ItemStack toItemStack, IDataComponentType<T> key) {
        T value = fromItemStack.get(key);
        toItemStack.set(key, value);
    }

    private void abort(Player player, String op, String reason) {
        ModLog.info("abort {} request of the '{}', reason: '{}', from: '{}', to: '{}'", op, player.getScoreboardName(), reason, player.getItemInHand(hand), itemStack);
    }
}
