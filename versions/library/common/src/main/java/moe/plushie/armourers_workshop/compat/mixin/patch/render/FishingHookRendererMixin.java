package moe.plushie.armourers_workshop.compat.mixin.patch.render;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.compat.client.event.AbstractRenderEntityEvent;
import moe.plushie.armourers_workshop.core.client.render.model.SinglePlaceholderModel;
import moe.plushie.armourers_workshop.core.client.render.state.FishingHookRenderState;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.init.event.client.RenderEntityEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.16, 1.26)")
@Mixin(FishingHookRenderer.class)
public class FishingHookRendererMixin<T extends FishingHook, S extends FishingHookRenderState> implements IEntityModel.Provider<S> {

    @Unique
    private final SinglePlaceholderModel<S> aw2$transformModel = new SinglePlaceholderModel<>();

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "HEAD"))
    public void aw2$willRender(T entity, float f, float g, PoseStack poseStack, MultiBufferSource bufferSource, int i, CallbackInfo ci) {
        EventManager.post(RenderEntityEvent.Setup.class, AbstractRenderEntityEvent.setup(entity, g, EntityRenderer.class.cast(this)));
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", ordinal = 1, shift = At.Shift.AFTER))
    public void aw2$render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource bufferSource, int i, CallbackInfo ci) {
        EventManager.post(RenderEntityEvent.Pre.class, AbstractRenderEntityEvent.pre(entity, i, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, EntityRenderer.class.cast(this)));
        if (!aw2$transformModel.root.isVisible()) {
            poseStack.setIdentity();
            poseStack.scale(0, 0, 0);
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "RETURN"))
    public void aw2$didRender(T entity, float f, float g, PoseStack poseStack, MultiBufferSource bufferSource, int i, CallbackInfo ci) {
        EventManager.post(RenderEntityEvent.Post.class, AbstractRenderEntityEvent.post(entity, i, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, EntityRenderer.class.cast(this)));
    }

    @Override
    public IEntityModel<S> abi$getEntityModel(S renderState) {
        return aw2$transformModel;
    }
}
