package moe.plushie.armourers_workshop.compat.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.state.IEntityRenderState;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractRenderState;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

@Available("[16, 26)")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractEntityRenderer<T extends Entity, S extends EntityRenderState> extends AbstractEntityRendererImpl<T, S> implements IEntityRenderer<T, S> {

    public AbstractEntityRenderer(Context context) {
        super(context);
    }

    public static <T extends Entity, S extends IEntityRenderState> IEntityRenderer<T, S> wrap(EntityRenderer<?> renderer) {
        return Objects.unsafeCast(renderer);
    }

    public static <T extends Entity> EntityRenderer<T> unwrap(IEntityRenderer<T, ?> renderer) {
        return Objects.unsafeCast(renderer);
    }

    protected void abi$render(S renderState, int lightmap, int overlay, IGraphicsContext context) {
        context.draw(AbstractGraphicsRenderer.invoke(super::render));
    }

    protected boolean abi$shouldShowName(T entity, double d) {
        return super.shouldShowName(entity);
    }

    @Override
    public final void render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource bufferSource, int lightmap) {
        var context = AbstractGraphicsRenderer.wrap(entity, f, g, poseStack, bufferSource, lightmap);
        abi$render(AbstractRenderState.createAndExtract(entity, f, g), lightmap, OverlayTexture.NO_OVERLAY, context);
    }

    @Override
    public final boolean shouldRender(T entity, Frustum frustum, double d, double e, double f) {
        return super.shouldRender(entity, frustum, d, e, f);
    }

    @Override
    public final boolean shouldShowName(T entity) {
        return abi$shouldShowName(entity, 0);
    }

    @Override
    public final ResourceLocation getTextureLocation(T entity) {
        // TODO: 26.1
        return null; // check not support get the texture?
    }
}
