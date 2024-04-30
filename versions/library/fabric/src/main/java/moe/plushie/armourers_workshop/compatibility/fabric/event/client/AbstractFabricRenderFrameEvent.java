package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderFrameEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

@Available("[1.16, )")
public class AbstractFabricRenderFrameEvent {

    public static IEventHandler<RenderFrameEvent.Pre> preFactory() {
        return subscriber -> ClientTickEvents.START_CLIENT_TICK.register(client -> subscriber.accept(() -> 0));
    }

    public static IEventHandler<RenderFrameEvent.Post> postFactory() {
        return subscriber -> ClientTickEvents.END_CLIENT_TICK.register(client -> subscriber.accept(() -> 0));
    }
}
