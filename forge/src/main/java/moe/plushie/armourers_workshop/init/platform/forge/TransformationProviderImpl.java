package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class TransformationProviderImpl {

    public static BakedModel handleTransforms(IPoseStack matrixStack, BakedModel bakedModel, ItemTransforms.TransformType transformType, boolean leftHandHackery) {
        return ForgeHooksClient.handleCameraTransforms(matrixStack.cast(), bakedModel, transformType, leftHandHackery);
    }
}
