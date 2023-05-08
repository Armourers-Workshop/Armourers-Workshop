package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(value = EnvType.CLIENT)
public abstract class AbstractAdvancedGuideRenderer {

    public abstract void render(PoseStack poseStack, int light, int overlay, float r, float g, float b, float alpha, MultiBufferSource buffers);
}
