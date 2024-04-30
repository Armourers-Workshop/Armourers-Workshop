package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.client.ClientPlayerEvent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Available("[1.16, )")
public class AbstractFabricClientPlayerEvent {

    public static IEventHandler<ClientPlayerEvent.LoggingIn> loggingInFactory() {
        return subscriber -> ClientPlayConnectionEvents.INIT.register((listener, client) -> subscriber.accept(() -> client.player));
    }

    public static IEventHandler<ClientPlayerEvent.LoggingOut> loggingOutFactory() {
        return subscriber -> ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> subscriber.accept(() -> client.player));
    }
}
