package moe.plushie.armourers_workshop.init.client;

import moe.plushie.armourers_workshop.builder.network.UndoActionPacket;
import moe.plushie.armourers_workshop.core.network.OpenWardrobePacket;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModKeyBindings;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.OpenKeyModifier;
import net.minecraft.client.gui.screens.Screen;

public class InputMotionHandler {

    public static void sendOpenWardrobe() {
        var player = EnvironmentManager.getPlayer();
        if (player != null && ModConfig.Common.canOpenWardrobe(player, player)) {
            NetworkManager.sendToServer(new OpenWardrobePacket(player));
        }
    }

    public static void sendUndo() {
        var isRedo = Screen.hasShiftDown();
        if (ModKeyBindings.UNDO_KEY.keyModifier() == OpenKeyModifier.SHIFT) {
            // If the player set shift key to undo key binding,
            // we will change the control key to redo key modifier.
            isRedo = Screen.hasControlDown();
        }
        NetworkManager.sendToServer(new UndoActionPacket(isRedo));
    }
}
