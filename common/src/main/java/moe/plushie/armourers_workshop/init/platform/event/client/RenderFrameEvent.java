package moe.plushie.armourers_workshop.init.platform.event.client;

public interface RenderFrameEvent {

    interface Pre {

        float getPartialTick();
    }

    interface Post {

        float getPartialTick();
    }
}
