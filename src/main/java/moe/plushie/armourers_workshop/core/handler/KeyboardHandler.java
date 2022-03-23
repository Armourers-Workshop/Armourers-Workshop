package moe.plushie.armourers_workshop.core.handler;

import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.OpenWardrobePacket;
import moe.plushie.armourers_workshop.core.utils.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class KeyboardHandler {

    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (KeyBindings.OPEN_WARDROBE_KEY.consumeClick()) {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                NetworkHandler.getInstance().sendToServer(new OpenWardrobePacket(player));
            }
        }
    }
}
