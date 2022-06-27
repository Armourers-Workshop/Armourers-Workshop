package moe.plushie.armourers_workshop.core.handler;

import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.OpenWardrobePacket;
import moe.plushie.armourers_workshop.core.network.packet.UndoActionPacket;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.utils.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class KeyboardHandler {

    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (KeyBindings.OPEN_WARDROBE_KEY.consumeClick()) {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null && ModConfig.Common.canOpenWardrobe(player, player)) {
                NetworkHandler.getInstance().sendToServer(new OpenWardrobePacket(player));
            }
        }
        if (KeyBindings.UNDO_KEY.consumeClick()) {
            boolean isRedo = Screen.hasShiftDown();
            if (KeyBindings.UNDO_KEY.getKeyModifier() == KeyModifier.SHIFT) {
                // If the player set shift key to undo key binding,
                // we will change the control key to redo key modifier.
                isRedo = Screen.hasControlDown();
            }
            NetworkHandler.getInstance().sendToServer(new UndoActionPacket(isRedo));
        }
    }
}
