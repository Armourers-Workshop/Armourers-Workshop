package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.network.ExecuteAlertPacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class UserNotifications {

    public static void sendErrorMessage(Component message, Player player) {
        Component title = Component.translatable("commands.armourers_workshop.notify.title");
        Component confirm = Component.translatable("commands.armourers_workshop.notify.confirm");
        sendToPlayer(new ExecuteAlertPacket(title, message, confirm, 1, null), player);
    }

    public static void sendSystemMessage(Component message, Player player) {
        Component title = Component.translatable("commands.armourers_workshop.notify.title");
        Component confirm = Component.translatable("commands.armourers_workshop.notify.confirm");
        sendToPlayer(new ExecuteAlertPacket(title, message, confirm, 0, null), player);
    }

//    public static void sendImportantToast(Component message, CompoundTag tag, Player player) {
//        Component title = Component.translatable("commands.armourers_workshop.notify.title");
//        Component confirm = Component.translatable("commands.armourers_workshop.notify.confirm");
//        sendToPlayer(new ExecuteAlertPacket(title, message, confirm, 0x80000001, tag), player);
//    }

    public static void sendSystemToast(Component message, CompoundTag tag, Player player) {
        Component title = Component.translatable("commands.armourers_workshop.notify.title");
        Component confirm = Component.translatable("commands.armourers_workshop.notify.confirm");
        sendToPlayer(new ExecuteAlertPacket(title, message, confirm, 0x80000000, tag), player);
    }

    private static void sendToPlayer(ExecuteAlertPacket packet, Player player) {
        ServerPlayer serverPlayer = ObjectUtils.safeCast(player, ServerPlayer.class);
        if (serverPlayer != null) {
            NetworkManager.sendToTracking(packet, serverPlayer);
        }
    }
}
