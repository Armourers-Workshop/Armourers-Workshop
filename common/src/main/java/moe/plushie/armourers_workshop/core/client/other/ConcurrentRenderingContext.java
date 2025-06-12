package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import org.jetbrains.annotations.NotNull;

public interface ConcurrentRenderingContext {

    boolean shouldRenderOutline();

    int overlay();

    int lightmap();

    int outlineColor();

    float partialTicks();

    double animationTicks();

    float renderPriority();

    SkinItemSource itemSource();

    IPoseStack poseStack();

    IBufferSource bufferSource();

    default IVertexConsumer getBuffer(IRenderType renderType) {
        return bufferSource().getBuffer(renderType);
    }

    ConcurrentBufferBuilder getBuffer(@NotNull BakedSkin skin);

    IPoseStack modelViewStack();
}
