package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.texture.TextureData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class AdvancedHorseGuideRenderer extends AdvancedEntityGuideRenderer {

    public AdvancedHorseGuideRenderer() {
    }

    @Override
    public BakedArmature getArmature() {
        return BakedArmature.defaultBy(Armatures.HORSE);
    }

    @Override
    public TextureData getTexture() {
        return new TextureData(ModTextures.HORSE_DEFAULT.toString(), 64, 64);
    }
}
