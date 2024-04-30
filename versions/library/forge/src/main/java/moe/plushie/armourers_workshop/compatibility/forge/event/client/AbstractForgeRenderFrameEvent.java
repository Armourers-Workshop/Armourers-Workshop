package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderFrameEvent;

@Available("[1.21, )")
public class AbstractForgeRenderFrameEvent {

    public static IEventHandler<RenderFrameEvent.Pre> preFactory() {
        return AbstractForgeClientEventsImpl.RENDER_FRAME_PRE.map(event -> () -> 0);
    }

    public static IEventHandler<RenderFrameEvent.Post> postFactory() {
        return AbstractForgeClientEventsImpl.RENDER_FRAME_POST.map(event -> () -> 0);
    }
}
