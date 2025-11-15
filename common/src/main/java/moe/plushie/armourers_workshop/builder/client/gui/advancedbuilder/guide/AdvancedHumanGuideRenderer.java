package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.init.ModTextures;

public class AdvancedHumanGuideRenderer extends AdvancedEntityGuideRenderer {

    @Override
    public BakedArmature armature() {
        return BakedArmature.defaultBy(Armatures.HUMANOID);
    }

    @Override
    public SkinTextureData texture() {
        return new SkinTextureData(ModTextures.MANNEQUIN_DEFAULT.toString(), 64, 64);
    }
}
