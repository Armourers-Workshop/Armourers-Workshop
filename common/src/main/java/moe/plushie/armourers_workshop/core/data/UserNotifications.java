package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.network.ExecuteAlertPacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class UserNotifications {

    public static void sendErrorMessage(Component message, Player player) {
        Component title = TranslateUtils.title("inventory.armourers_workshop.common.text.error");
        Component confirm = TranslateUtils.title("inventory.armourers_workshop.common.button.ok");
        sendToPlayer(new ExecuteAlertPacket(title, message, confirm, 1, null), player);
    }

    public static void sendSystemMessage(Component message, Player player) {
        Component title = TranslateUtils.title("inventory.armourers_workshop.common.text.info");
        Component confirm = TranslateUtils.title("inventory.armourers_workshop.common.button.ok");
        sendToPlayer(new ExecuteAlertPacket(title, message, confirm, 0, null), player);
    }

    private static void sendToPlayer(ExecuteAlertPacket packet, Player player) {
        ServerPlayer serverPlayer = ObjectUtils.safeCast(player, ServerPlayer.class);
        if (serverPlayer != null) {
            NetworkManager.sendToTracking(packet, serverPlayer);
        }
    }
}
