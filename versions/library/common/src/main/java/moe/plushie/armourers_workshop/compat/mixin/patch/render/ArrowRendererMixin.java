package moe.plushie.armourers_workshop.compat.mixin.patch.render;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.compat.client.event.AbstractRenderEntityEvent;
import moe.plushie.armourers_workshop.core.client.render.model.SinglePlaceholderModel;
import moe.plushie.armourers_workshop.core.client.render.state.ArrowRenderState;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.init.event.client.RenderEntityEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.16, 1.22)")
@Mixin(ArrowRenderer.class)
public class ArrowRendererMixin<T extends AbstractArrow, S extends ArrowRenderState> implements IEntityModel.Provider<S> {

    @Unique
    private final SinglePlaceholderModel<S> aw2$transformModel = new SinglePlaceholderModel<>();

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/AbstractArrow;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "HEAD"))
    public void aw2$willRender(T entity, float p_225623_2_, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        EventManager.post(RenderEntityEvent.Setup.class, AbstractRenderEntityEvent.setup(entity, partialTick, EntityRenderer.class.cast(this)));
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/AbstractArrow;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", shift = At.Shift.BEFORE))
    public void aw2$render(T entity, float p_225623_2_, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int lightmap, CallbackInfo ci) {
        EventManager.post(RenderEntityEvent.Pre.class, AbstractRenderEntityEvent.pre(entity, lightmap, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, EntityRenderer.class.cast(this)));
        if (!aw2$transformModel.root.isVisible()) {
            poseStack.setIdentity();
            poseStack.scale(0, 0, 0);
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/AbstractArrow;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "RETURN"))
    public void aw2$didRender(T entity, float p_225623_2_, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int lightmap, CallbackInfo ci) {
        EventManager.post(RenderEntityEvent.Post.class, AbstractRenderEntityEvent.post(entity, lightmap, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, EntityRenderer.class.cast(this)));
    }

    @Override
    public IEntityModel<S> abi$getEntityModel(S renderState) {
        return aw2$transformModel;
    }
}
