package moe.plushie.armourers_workshop.compat.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.event.ClientFrameRenderEvents;

@Available("[1.16, )")
public class AbstractFabricRenderFrameEvent {

    public static IEventHandler<RenderFrameEvent.Pre> preFactory() {
        return (priority, receiveCancelled, subscriber) -> ClientFrameRenderEvents.START.register(delta -> subscriber.accept(() -> delta));
    }

    public static IEventHandler<RenderFrameEvent.Post> postFactory() {
        return (priority, receiveCancelled, subscriber) -> ClientFrameRenderEvents.END.register(delta -> subscriber.accept(() -> delta));
    }
}
