package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import org.jetbrains.annotations.NotNull;

public interface ConcurrentRenderingContext {

    boolean shouldRenderOutline();

    int getOverlay();

    int getLightmap();

    int getOutlineColor();

    float getPartialTicks();

    double getAnimationTicks();

    float getRenderPriority();

    SkinItemSource getItemSource();

    IPoseStack getPoseStack();

    IBufferSource getBufferSource();

    default IVertexConsumer getBuffer(IRenderType renderType) {
        return getBufferSource().getBuffer(renderType);
    }

    ConcurrentBufferBuilder getBuffer(@NotNull BakedSkin skin);

    IPoseStack getModelViewStack();
}
