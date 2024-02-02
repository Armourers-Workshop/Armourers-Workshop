package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(EnvType.CLIENT)
public abstract class AbstractAdvancedGuideRenderer {

    public abstract void render(SkinDocument document, PoseStack poseStack, int light, int overlay, MultiBufferSource buffers);
}
