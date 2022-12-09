package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;

@Environment(value = EnvType.CLIENT)
public class TransformationProvider {

    @ExpectPlatform
    public static BakedModel handleTransforms(IPoseStack matrixStack, BakedModel bakedModel, ItemTransforms.TransformType transformType, boolean leftHandHackery) {
        return bakedModel;
    }
}
