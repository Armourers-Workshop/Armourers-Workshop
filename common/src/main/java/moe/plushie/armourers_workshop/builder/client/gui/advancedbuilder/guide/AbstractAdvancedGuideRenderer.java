package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocument;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractAdvancedGuideRenderer {

    public abstract void render(SkinDocument document, IPoseStack poseStack, int light, int overlay, IBufferSource bufferSource);
}
