package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

public interface ConcurrentRenderingContext {

    int getOverlay();

    int getLightmap();

    float getPartialTicks();

    float getAnimationTicks();

    float getRenderPriority();

    SkinItemSource getItemSource();

    IPoseStack getPoseStack();

    IBufferSource getBufferSource();

    default IVertexConsumer getBuffer(RenderType renderType) {
        return getBufferSource().getBuffer(renderType);
    }

    ConcurrentBufferBuilder getBuffer(@NotNull BakedSkin skin);

    IPoseStack getModelViewStack();
}
