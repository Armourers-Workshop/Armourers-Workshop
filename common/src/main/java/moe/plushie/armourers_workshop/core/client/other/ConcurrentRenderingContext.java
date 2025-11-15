package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;

public interface ConcurrentRenderingContext extends IGraphicsContext {

    default int outlineColor() {
        return 0x00000000;
    }

    default int overlay() {
        return OverlayTexture.NO_OVERLAY;
    }

    default int lightmap() {
        return LightmapTexture.DEFAULT;
    }

    default float partialTicks() {
        return 1.0f;
    }

    default double animationTicks() {
        return 0.0;
    }

    default AnimationManager animationManager() {
        return AnimationManager.NONE;
    }

    default SkinItemSource itemSource() {
        return SkinItemSource.EMPTY;
    }

    /**
     * Create an empty context.
     */
    static ConcurrentRenderingContext empty() {
        var poseStack = new OpenPoseStack();
        return new ConcurrentRenderingContext() {

            @Override
            public void draw(IGraphicsElement element) {
                // not support.
            }

            @Override
            public IPoseStack ctm() {
                return poseStack;
            }
        };
    }
}
