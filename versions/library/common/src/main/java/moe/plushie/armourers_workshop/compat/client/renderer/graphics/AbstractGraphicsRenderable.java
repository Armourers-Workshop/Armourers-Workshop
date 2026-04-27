package moe.plushie.armourers_workshop.compat.client.renderer.graphics;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import net.minecraft.client.renderer.MultiBufferSource;

@Available("[16, 26)")
@OnlyIn(Dist.CLIENT)
public interface AbstractGraphicsRenderable {

    void render(PoseStack poseStack, MultiBufferSource bufferSource);
}
