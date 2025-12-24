package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderable;

@OnlyIn(Dist.CLIENT)
public interface SceneGraphicsRenderer {

    void submit(IGraphicsRenderable renderable);

    void submit(AbstractGraphicsRenderable renderable);

    default void flush() {
        // nop
    }

    IPoseStack poseStack();
}
