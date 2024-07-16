package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderFrameEvent;
import net.minecraft.client.Minecraft;

@Available("[1.21, )")
public class AbstractForgeRenderFrameEvent {

    public static IEventHandler<RenderFrameEvent.Pre> preFactory() {
        return AbstractForgeClientEventsImpl.RENDER_FRAME_PRE.map(event -> ReusableEvent.INSTANCE);
    }

    public static IEventHandler<RenderFrameEvent.Post> postFactory() {
        return AbstractForgeClientEventsImpl.RENDER_FRAME_POST.map(event -> ReusableEvent.INSTANCE);
    }

    private static class ReusableEvent implements RenderFrameEvent.Pre, RenderFrameEvent.Post {

        private static final ReusableEvent INSTANCE = new ReusableEvent();

        @Override
        public boolean isPaused() {
            return Minecraft.getInstance().isPaused();
        }

        @Override
        public boolean isFrozen() {
            var minecraft = Minecraft.getInstance();
            return minecraft.level != null && !minecraft.level.tickRateManager().runsNormally();
        }
    }
}
