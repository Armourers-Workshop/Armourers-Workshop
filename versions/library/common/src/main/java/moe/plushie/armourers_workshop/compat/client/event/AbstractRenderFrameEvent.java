package moe.plushie.armourers_workshop.compat.client.event;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractDeltaTracker;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import net.minecraft.client.DeltaTracker;

@Available("[1.21, )")
public class AbstractRenderFrameEvent {

    public static RenderFrameEvent.Pre pre(DeltaTracker deltaTracker) {
        var deltaTracker1 = AbstractDeltaTracker.of(deltaTracker);
        return () -> deltaTracker1;
    }

    public static RenderFrameEvent.Post post(DeltaTracker deltaTracker) {
        var deltaTracker1 = AbstractDeltaTracker.of(deltaTracker);
        return () -> deltaTracker1;
    }
}
