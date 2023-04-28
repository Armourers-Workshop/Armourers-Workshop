package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.api.common.IItemTransformType;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class TransformationProviderImpl {

    public static BakedModel handleTransforms(IPoseStack poseStack, BakedModel bakedModel, IItemTransformType transformType, boolean leftHandHackery) {
        bakedModel.getTransforms().getTransform(ItemTransforms.ofType(transformType)).apply(leftHandHackery, poseStack.cast());
        return bakedModel;
    }

}
