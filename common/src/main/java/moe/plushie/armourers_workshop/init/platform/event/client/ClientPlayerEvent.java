package moe.plushie.armourers_workshop.init.platform.event.client;

import net.minecraft.world.entity.player.Player;

public interface ClientPlayerEvent {

    Player getPlayer();

    interface LoggingIn extends ClientPlayerEvent {
    }

    interface LoggingOut extends ClientPlayerEvent {
    }

    interface Clone extends ClientPlayerEvent {

        Player getOldPlayer();

        Player getNewPlayer();

        @Override
        default Player getPlayer() {
            return getNewPlayer();
        }
    }
}
