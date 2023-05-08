package moe.plushie.armourers_workshop.init.platform.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.common.IItemTransformType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class TransformationProviderImpl {

    public static BakedModel handleTransforms(PoseStack poseStack, BakedModel bakedModel, IItemTransformType transformType, boolean leftHandHackery) {
        return ForgeHooksClient.handleCameraTransforms(poseStack, bakedModel, ItemTransforms.ofType(transformType), leftHandHackery);
    }
}
