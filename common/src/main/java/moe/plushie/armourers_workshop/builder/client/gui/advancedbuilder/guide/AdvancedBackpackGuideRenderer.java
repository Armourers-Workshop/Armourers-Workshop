package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AdvancedBackpackGuideRenderer extends AdvancedEntityGuideRenderer {

    @Override
    public void applyOffset(SkinDocument document, IPoseStack poseStack) {
        poseStack.translate(0, 0, -2); // move to back
    }

    @Override
    protected void renderOutline(OpenRectangle3f rect, int color, IPoseStack poseStack, IBufferSource bufferSource) {
        // nope.
    }

    @Override
    public BakedArmature armature() {
        return BakedArmature.defaultBy(Armatures.HUMANOID);
    }

    @Override
    public SkinTextureData texture() {
        return new SkinTextureData(ModTextures.MANNEQUIN_DEFAULT.toString(), 64, 64);
    }
}
