package moe.plushie.armourers_workshop.init.event.client;

import net.minecraft.world.entity.player.Player;

public interface ClientPlayerEvent {

    Player player();

    interface LoggingIn extends ClientPlayerEvent {
    }

    interface LoggingOut extends ClientPlayerEvent {
    }

    interface Clone extends ClientPlayerEvent {

        Player oldPlayer();

        Player newPlayer();

        @Override
        default Player player() {
            return newPlayer();
        }
    }
}
