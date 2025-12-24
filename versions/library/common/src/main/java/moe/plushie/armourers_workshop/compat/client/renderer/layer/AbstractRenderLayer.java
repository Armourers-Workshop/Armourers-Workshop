package moe.plushie.armourers_workshop.compat.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;

@Available("[1.16, 1.22)")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractRenderLayer<T extends Entity, S extends EntityRenderState, M extends IEntityModel<S>> extends RenderLayer<T, EntityModel<T>> {

    public AbstractRenderLayer(IEntityRenderer<T, S> renderer) {
        super(Objects.unsafeCast(renderer)); // cast to RenderLayerParent
    }

    protected abstract void abi$render(S renderState, int lightmap, int overlay, float netHeadYaw, float headPitch, IGraphicsContext context);

    @Override
    public final void render(PoseStack poseStack, MultiBufferSource bufferSource, int lightmap, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        var context = AbstractGraphicsRenderer.wrap(poseStack, bufferSource);
        abi$render(AbstractRenderState.wrap(entity), lightmap, OverlayTexture.NO_OVERLAY, netHeadYaw, headPitch, context);
    }
}
