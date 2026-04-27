package moe.plushie.armourers_workshop.compat.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.event.client.RenderScreenEvent;

@Available("[16, )")
public class AbstractForgeRenderScreenEvent {

    public static IEventHandler<RenderScreenEvent.Pre> preFactory() {
        return AbstractForgeClientEventsImpl.RENDER_SCREEN_PRE.map(event -> () -> null);
    }

    public static IEventHandler<RenderScreenEvent.Post> postFactory() {
        return AbstractForgeClientEventsImpl.RENDER_SCREEN_POST.map(event -> () -> null);
    }
}
