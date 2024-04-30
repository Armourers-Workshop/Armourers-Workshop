package moe.plushie.armourers_workshop.init.platform.event.client;

import net.minecraft.world.entity.player.Player;

public interface ClientPlayerEvent {

    interface LoggingIn {
        Player getPlayer();
    }

    interface LoggingOut {
        Player getPlayer();
    }
}
