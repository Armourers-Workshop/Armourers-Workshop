package moe.plushie.armourers_workshop.api.client.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface IGuideRenderer {

    void render(PoseStack poseStack, IGuideDataProvider provider, int light, int overlay, MultiBufferSource multiBufferSource);
}
