package moe.plushie.armourers_workshop.compat.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.client.event.AbstractRenderFrameEvent;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;

@Available("[21, )")
public class AbstractForgeRenderFrameEvent {

    public static IEventHandler<RenderFrameEvent.Pre> preFactory() {
        return AbstractForgeClientEventsImpl.RENDER_FRAME_PRE.map(event -> AbstractRenderFrameEvent.pre(event.getPartialTick()));
    }

    public static IEventHandler<RenderFrameEvent.Post> postFactory() {
        return AbstractForgeClientEventsImpl.RENDER_FRAME_POST.map(event -> AbstractRenderFrameEvent.post(event.getPartialTick()));
    }
}
