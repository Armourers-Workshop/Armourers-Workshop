package moe.plushie.armourers_workshop.init.event.client;

import moe.plushie.armourers_workshop.compat.core.AbstractDeltaTracker;

public interface RenderFrameEvent {

    AbstractDeltaTracker deltaTracker();

    interface Pre extends RenderFrameEvent {

    }

    interface Post extends RenderFrameEvent {
    }
}
