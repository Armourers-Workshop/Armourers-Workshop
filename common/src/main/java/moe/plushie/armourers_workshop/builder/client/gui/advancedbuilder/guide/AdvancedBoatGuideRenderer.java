package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AdvancedBoatGuideRenderer extends AdvancedEntityGuideRenderer {

    @Override
    public BakedArmature armature() {
        return BakedArmature.defaultBy(Armatures.BOAT);
    }

    @Override
    public SkinTextureData texture() {
        return new SkinTextureData(ModTextures.BOAT_DEFAULT.toString(), 128, 64);
    }
}
