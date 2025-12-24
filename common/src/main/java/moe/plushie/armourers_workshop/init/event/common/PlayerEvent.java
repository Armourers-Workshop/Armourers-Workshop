package moe.plushie.armourers_workshop.init.event.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface PlayerEvent {

    Player player();

    interface LoggingIn extends PlayerEvent {
    }

    interface LoggingOut extends PlayerEvent {
    }

    interface Death extends PlayerEvent {
    }

    interface Clone extends PlayerEvent {

        Player original();
    }

    interface StartTracking extends PlayerEvent {

        Entity target();
    }

    interface Attack extends PlayerEvent {

        Entity target();

        void setCancelled(boolean isCancelled);
    }
}
