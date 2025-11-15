package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractGraphicsRenderable;

public interface OpenGraphicsRenderer {

    void submit(IGraphicsRenderable renderable);

    void submit(AbstractGraphicsRenderable renderable);

    default void flush() {
        // nop
    }

    IPoseStack poseStack();
}
