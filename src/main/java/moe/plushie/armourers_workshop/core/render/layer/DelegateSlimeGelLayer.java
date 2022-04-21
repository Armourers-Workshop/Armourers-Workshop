package moe.plushie.armourers_workshop.core.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeGelLayer;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class DelegateSlimeGelLayer<T extends LivingEntity> extends SlimeGelLayer<T> {

    protected final SlimeGelLayer<T> slimeGelLayer;

    public DelegateSlimeGelLayer(IEntityRenderer<T, SlimeModel<T>> entityRenderer, SlimeGelLayer<T> slimeGelLayer) {
        super(entityRenderer);
        this.slimeGelLayer = slimeGelLayer;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffers, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        slimeGelLayer.render(matrixStack, buffers, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }
}