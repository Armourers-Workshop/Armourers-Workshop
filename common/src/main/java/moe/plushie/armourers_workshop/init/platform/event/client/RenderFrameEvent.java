package moe.plushie.armourers_workshop.init.platform.event.client;

public interface RenderFrameEvent {

    boolean isPaused();

    boolean isFrozen();

    interface Pre extends RenderFrameEvent {

    }

    interface Post extends RenderFrameEvent {
    }
}
