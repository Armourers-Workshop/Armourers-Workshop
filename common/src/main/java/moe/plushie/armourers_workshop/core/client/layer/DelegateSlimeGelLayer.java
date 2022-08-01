package moe.plushie.armourers_workshop.core.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public class DelegateSlimeGelLayer<T extends LivingEntity> extends SlimeOuterLayer<T> {

    protected final SlimeOuterLayer<T> slimeGelLayer;

    public DelegateSlimeGelLayer(RenderLayerParent<T, SlimeModel<T>> entityRenderer, SlimeOuterLayer<T> slimeGelLayer) {
        super(entityRenderer);
        this.slimeGelLayer = slimeGelLayer;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffers, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        slimeGelLayer.render(matrixStack, buffers, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }
}