package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.client.ClientPlayerEvent;

@Available("[1.16, )")
public class AbstractForgeClientPlayerEvent {

    public static IEventHandler<ClientPlayerEvent.LoggingIn> loggingInFactory() {
        return AbstractForgeClientEventsImpl.PLAYER_LOGIN.map(event -> event::getPlayer);
    }

    public static IEventHandler<ClientPlayerEvent.LoggingOut> loggingOutFactory() {
        return AbstractForgeClientEventsImpl.PLAYER_LOGOUT.map(event -> event::getPlayer);
    }
}
