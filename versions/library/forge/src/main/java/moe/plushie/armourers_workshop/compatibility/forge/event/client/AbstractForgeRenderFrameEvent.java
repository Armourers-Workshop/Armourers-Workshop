package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.core.AbstractDeltaTracker;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;

@Available("[1.21, )")
public class AbstractForgeRenderFrameEvent {

    public static IEventHandler<RenderFrameEvent.Pre> preFactory() {
        return AbstractForgeClientEventsImpl.RENDER_FRAME_PRE.map(event -> {
            var minecraft = EnvironmentManager.getClient();
            var delta = new AbstractDeltaTracker(minecraft.level, event.getPartialTick(), minecraft.isPaused());
            return () -> delta;
        });
    }

    public static IEventHandler<RenderFrameEvent.Post> postFactory() {
        return AbstractForgeClientEventsImpl.RENDER_FRAME_POST.map(event -> {
            var minecraft = EnvironmentManager.getClient();
            var delta = new AbstractDeltaTracker(minecraft.level, event.getPartialTick(), minecraft.isPaused());
            return () -> delta;
        });
    }
}
