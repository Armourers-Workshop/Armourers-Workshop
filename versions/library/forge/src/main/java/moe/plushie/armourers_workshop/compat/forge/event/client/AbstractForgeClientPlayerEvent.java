package moe.plushie.armourers_workshop.compat.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.event.client.ClientPlayerEvent;
import net.minecraft.world.entity.player.Player;

@Available("[1.16, )")
public class AbstractForgeClientPlayerEvent {

    public static IEventHandler<ClientPlayerEvent.LoggingIn> loggingInFactory() {
        return AbstractForgeClientEventsImpl.PLAYER_LOGIN.map(event -> event::getPlayer);
    }

    public static IEventHandler<ClientPlayerEvent.LoggingOut> loggingOutFactory() {
        return AbstractForgeClientEventsImpl.PLAYER_LOGOUT.map(event -> event::getPlayer);
    }

    public static IEventHandler<ClientPlayerEvent.Clone> cloneFactory() {
        return AbstractForgeClientEventsImpl.PLAYER_CLONE.map(event -> new ClientPlayerEvent.Clone() {
            @Override
            public Player oldPlayer() {
                return event.getOldPlayer();
            }

            @Override
            public Player newPlayer() {
                return event.getNewPlayer();
            }
        });
    }
}
