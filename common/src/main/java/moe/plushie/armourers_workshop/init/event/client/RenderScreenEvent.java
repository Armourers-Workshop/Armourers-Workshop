package moe.plushie.armourers_workshop.init.event.client;

import com.apple.library.coregraphics.CGGraphicsContext;

public interface RenderScreenEvent {

    CGGraphicsContext context();

    interface Pre extends RenderScreenEvent {

    }

    interface Post extends RenderScreenEvent {
    }
}
