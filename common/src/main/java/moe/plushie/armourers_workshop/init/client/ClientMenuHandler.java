package moe.plushie.armourers_workshop.init.client;

import com.apple.library.impl.InputManagerImpl;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.builder.network.UndoActionPacket;
import moe.plushie.armourers_workshop.core.network.OpenWardrobePacket;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModKeyBindings;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.OpenKeyModifier;
import net.minecraft.client.Minecraft;

@OnlyIn(Dist.CLIENT)
public class ClientMenuHandler {

    public static void sendOpenWardrobePacket() {
        var player = Minecraft.getInstance().player;
        if (player != null && ModConfig.Common.canOpenWardrobe(player, player)) {
            NetworkManager.sendToServer(new OpenWardrobePacket(player));
        }
    }

    public static void sendUndoPacket() {
        var isRedo = InputManagerImpl.hasShiftDown();
        if (ModKeyBindings.UNDO_KEY.keyModifier() == OpenKeyModifier.SHIFT) {
            // If the player set shift key to undo key binding,
            // we will change the control key to redo key modifier.
            isRedo = InputManagerImpl.hasControlDown();
        }
        NetworkManager.sendToServer(new UndoActionPacket(isRedo));
    }
}
