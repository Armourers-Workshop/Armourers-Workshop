package extensions.net.minecraft.world.entity.player.Player;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

@Extension
public class PlayerExt {

    public static Abilities getAbilities(@This Player player) {
        return player.abilities;
    }

    public static Inventory getInventory(@This Player player) {
        return player.inventory;
    }

    public static void sendSystemMessage(@This Player player, Component text) {
        player.sendMessage(text, player.getUUID());
    }
}