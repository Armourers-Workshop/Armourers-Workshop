package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class AbstractRenderLayer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public AbstractRenderLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public final void render(PoseStack poseStack, MultiBufferSource buffers, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        render(entity, limbSwing, limbSwingAmount, packedLightIn, partialTicks, ageInTicks, netHeadYaw, headPitch, AbstractPoseStack.wrap(poseStack), AbstractBufferSource.wrap(buffers));
    }

    public abstract void render(T entity, float limbSwing, float limbSwingAmount, int packedLightIn, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, IPoseStack poseStack, IBufferSource bufferSource);
}
