package moe.plushie.armourers_workshop.init.platform.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientHooks;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;

@SuppressWarnings("unused")
public class TransformationProviderImpl {

    public static BakedModel handleTransforms(PoseStack poseStack, BakedModel bakedModel, AbstractItemTransformType transformType, boolean leftHandHackery) {
        return AbstractForgeClientHooks.handleCameraTransforms(poseStack, bakedModel, transformType, leftHandHackery);
    }
}
