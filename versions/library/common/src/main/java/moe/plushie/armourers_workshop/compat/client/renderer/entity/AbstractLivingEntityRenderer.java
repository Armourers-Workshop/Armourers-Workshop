package moe.plushie.armourers_workshop.compat.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.ILivingEntityRenderer;
import moe.plushie.armourers_workshop.api.client.state.ILivingEntityRenderState;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractRenderState;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.16, 1.22)")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<T>> extends AbstractLivingEntityRendererImpl<T, S, M> implements IEntityRenderer<T, S> {

    public AbstractLivingEntityRenderer(Context context, M entityModel, float shadowRadius) {
        super(context, entityModel, shadowRadius);
    }

    public static <T extends LivingEntity, S extends ILivingEntityRenderState, M extends IEntityModel<S>> ILivingEntityRenderer<T, S, M> wrap(LivingEntityRenderer<?, ?> renderer) {
        return Objects.unsafeCast(renderer);
    }

    protected void abi$render(S renderState, int lightmap, int overlay, IGraphicsContext context) {
        context.draw(AbstractGraphicsRenderer.invoke(super::render));
    }

    protected abstract OpenResourceLocation abi$getTextureLocation(S state);

    protected float abi$getEntityScale(S state) {
        return 1.0f;
    }

    protected OpenQuaternionf abi$getEntityRotations(S renderState) {
        return null;
    }

    protected boolean abi$shouldShowName(T entity, double d) {
        return super.shouldShowName(entity);
    }

    @Override
    public final void render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource bufferSource, int lightmap) {
        var context = AbstractGraphicsRenderer.wrap(entity, f, g, poseStack, bufferSource, lightmap);
        abi$render(AbstractRenderState.createAndExtract(entity, f, g), lightmap, OverlayTexture.NO_OVERLAY, context);
    }

//    @Override
//    public final boolean shouldRender(T entity, Frustum frustum, double d, double e, double f) {
//        return super.shouldRender(entity, frustum, d, e, f);
//    }

    @Override
    protected final boolean shouldShowName(T livingEntity) {
        return abi$shouldShowName(livingEntity, 0);
    }

    @Override
    public final ResourceLocation getTextureLocation(T entity) {
        var location = abi$getTextureLocation(AbstractRenderState.wrap(entity));
        if (location != null) {
            return location.toLocation();
        }
        return null;
    }

    @Override
    protected final float getEntityScale(T entity) {
        return abi$getEntityScale(AbstractRenderState.wrap(entity));
    }

    @Override
    protected final OpenQuaternionf getEntityRotations(T entity) {
        return abi$getEntityRotations(AbstractRenderState.wrap(entity));
    }
}

