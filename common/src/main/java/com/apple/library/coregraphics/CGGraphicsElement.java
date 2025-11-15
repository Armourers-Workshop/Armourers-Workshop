package com.apple.library.coregraphics;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;

public interface CGGraphicsElement {

    void render(IPoseStack poseStack, IBufferSource bufferSource);

    default void prepare(CGGraphicsContext context) {
        // nope
    }

    default boolean shouldOffscreenRender() {
        return true;
    }
}
