package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.registry.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.core.network.OpenWardrobePacket;
import moe.plushie.armourers_workshop.core.network.UndoActionPacket;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModKeyBindings;
import moe.plushie.armourers_workshop.utils.ext.KeyModifierX;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public class ClientBuilderManager {

    public static void sendOpenWardrobe() {
        Player player = Minecraft.getInstance().player;
        if (player != null && ModConfig.Common.canOpenWardrobe(player, player)) {
            NetworkManager.sendToServer(new OpenWardrobePacket(player));
        }
    }

    public static void sendUndo() {
        boolean isRedo = Screen.hasShiftDown();
        if (ModKeyBindings.UNDO_KEY.getKeyModifier() == KeyModifierX.SHIFT) {
            // If the player set shift key to undo key binding,
            // we will change the control key to redo key modifier.
            isRedo = Screen.hasControlDown();
        }
        NetworkManager.sendToServer(new UndoActionPacket(isRedo));
    }

    @ExpectPlatform
    public static Impl getInstance() {
        throw new AssertionError();
    }

    public interface Impl {

        <T extends IKeyBinding> IKeyBindingBuilder<T> createKeyBindingBuilder(String key);
    }
}
