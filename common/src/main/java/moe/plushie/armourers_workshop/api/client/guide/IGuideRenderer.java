package moe.plushie.armourers_workshop.api.client.guide;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface IGuideRenderer {

    void render(IPoseStack poseStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource multiBufferSource);
}
