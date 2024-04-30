package moe.plushie.armourers_workshop.init.platform.event.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface PlayerEvent {

    Player getPlayer();

    interface LoggingIn extends PlayerEvent {
    }

    interface LoggingOut extends PlayerEvent {
    }

    interface Death extends PlayerEvent {
    }

    interface Clone extends PlayerEvent {

        Player getOriginal();
    }

    interface StartTracking extends PlayerEvent {

        Entity getTarget();
    }

    interface Attack extends PlayerEvent {

        Entity getTarget();

        void setCancelled(boolean isCancelled);
    }
}
