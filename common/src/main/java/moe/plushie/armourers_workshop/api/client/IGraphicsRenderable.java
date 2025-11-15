package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;

public interface IGraphicsRenderable {

    IRenderType renderType();

    void render(IPoseStack.Pose pose, IVertexConsumer builder);

    default boolean shouldRender(IGraphicsContext context) {
        return true;
    }
}
