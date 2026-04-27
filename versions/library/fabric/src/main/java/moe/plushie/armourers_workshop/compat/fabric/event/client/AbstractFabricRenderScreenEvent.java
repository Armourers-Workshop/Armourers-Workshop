package moe.plushie.armourers_workshop.compat.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.event.client.RenderScreenEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.event.ClientScreenRenderEvents;

@Available("[16, )")
public class AbstractFabricRenderScreenEvent {

    public static IEventHandler<RenderScreenEvent.Pre> preFactory() {
        return (priority, receiveCancelled, subscriber) -> ClientScreenRenderEvents.START.register(client -> subscriber.accept(() -> null));
    }

    public static IEventHandler<RenderScreenEvent.Post> postFactory() {
        return (priority, receiveCancelled, subscriber) -> ClientScreenRenderEvents.END.register(client -> subscriber.accept(() -> null));
    }
}
