package extensions.net.minecraft.world.entity.player.Player;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.16, 1.19)")
public class SystemMessageProvider {

    public static void sendSystemMessage(@This Player player, Component text) {
        player.sendMessage(text, player.getUUID());
    }
}
