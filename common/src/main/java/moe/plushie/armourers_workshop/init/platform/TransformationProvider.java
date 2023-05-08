package moe.plushie.armourers_workshop.init.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.common.IItemTransformType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.BakedModel;

@Environment(value = EnvType.CLIENT)
public class TransformationProvider {

    @ExpectPlatform
    public static BakedModel handleTransforms(PoseStack poseStack, BakedModel bakedModel, IItemTransformType transformType, boolean leftHandHackery) {
        return bakedModel;
    }
}
