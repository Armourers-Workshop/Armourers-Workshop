package moe.plushie.armourers_workshop.compat.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.core.AbstractDeltaTracker;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import net.minecraft.client.Minecraft;

@Available("[1.21, )")
public class AbstractForgeRenderFrameEvent {

    public static IEventHandler<RenderFrameEvent.Pre> preFactory() {
        return AbstractForgeClientEventsImpl.RENDER_FRAME_PRE.map(event -> {
            var minecraft = Minecraft.getInstance();
            var delta = new AbstractDeltaTracker(minecraft.level, event.getPartialTick(), minecraft.isPaused());
            return () -> delta;
        });
    }

    public static IEventHandler<RenderFrameEvent.Post> postFactory() {
        return AbstractForgeClientEventsImpl.RENDER_FRAME_POST.map(event -> {
            var minecraft = Minecraft.getInstance();
            var delta = new AbstractDeltaTracker(minecraft.level, event.getPartialTick(), minecraft.isPaused());
            return () -> delta;
        });
    }
}
