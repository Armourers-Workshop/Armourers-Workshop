package moe.plushie.armourers_workshop.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(value = EnvType.CLIENT)
public interface IGuideRenderer {

    void render(PoseStack poseStack, IGuideProvider provider, int light, int overlay, MultiBufferSource multiBufferSource);
}
