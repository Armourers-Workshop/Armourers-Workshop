package moe.plushie.armourers_workshop.init.event.client;

import moe.plushie.armourers_workshop.api.common.IDeltaTracker;

public interface RenderFrameEvent {

    IDeltaTracker deltaTracker();

    interface Pre extends RenderFrameEvent {

    }

    interface Post extends RenderFrameEvent {
    }
}
