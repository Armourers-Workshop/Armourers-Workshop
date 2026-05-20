package moe.plushie.armourers_workshop.compat.fabric.client.event;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.event.client.ClientPlayerEvent;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Available("[16, )")
public class AbstractFabricClientPlayerEvent {

    public static IEventHandler<ClientPlayerEvent.LoggingIn> loggingInFactory() {
        return (priority, receiveCancelled, subscriber) -> ClientPlayConnectionEvents.JOIN.register((listener, sender, client) -> RenderSystem.safeCall(() -> subscriber.accept(() -> client.player)));
    }

    public static IEventHandler<ClientPlayerEvent.LoggingOut> loggingOutFactory() {
        return (priority, receiveCancelled, subscriber) -> ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> RenderSystem.safeCall(() -> subscriber.accept(() -> client.player)));
    }
}
