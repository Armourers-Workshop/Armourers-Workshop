package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.core.texture.TextureData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AdvancedHumanGuideRenderer extends AdvancedEntityGuideRenderer {

//    private static final ImmutableMap<ISkinPartType, Function<MannequinEntity, Rotations>> RR = ImmutableMap.<ISkinPartType, Function<MannequinEntity, Rotations>>builder()
//            .put(SkinPartTypes.BIPPED_LEFT_ARM, MannequinEntity::getLeftArmPose)
//            .put(SkinPartTypes.BIPPED_RIGHT_ARM, MannequinEntity::getRightArmPose)
//
//            .put(SkinPartTypes.BIPPED_LEFT_LEG, MannequinEntity::getLeftLegPose)
//            .put(SkinPartTypes.BIPPED_RIGHT_LEG, MannequinEntity::getRightLegPose)
//
//            .build();

    public AdvancedHumanGuideRenderer() {
    }

    @Override
    public BakedArmature getArmature() {
        return BakedArmature.defaultBy(Armatures.HUMANOID);
    }

    @Override
    public TextureData getTexture() {
        return new TextureData(ModTextures.MANNEQUIN_DEFAULT.toString(), 64, 64);
    }
}
