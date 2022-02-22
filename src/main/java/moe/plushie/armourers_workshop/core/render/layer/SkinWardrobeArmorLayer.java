package moe.plushie.armourers_workshop.core.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class SkinWardrobeArmorLayer<T extends LivingEntity, M extends BipedModel<T>> extends LayerRenderer<T, M> {

    public SkinWardrobeArmorLayer(IEntityRenderer<T, M> renderer) {
        super(renderer);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderType, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStack.pushPose();

        BipedModel<?> model = getParentModel();
        if (model.young) {
            float scale = 1.0f / model.babyBodyScale;
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(0.0f, model.bodyYOffset / 16.0f, 0.0f);
        }

        ClientWardrobeHandler.onRenderArmor(entity, model, packedLightIn, matrixStack, renderType);

        matrixStack.popPose();
    }
}
