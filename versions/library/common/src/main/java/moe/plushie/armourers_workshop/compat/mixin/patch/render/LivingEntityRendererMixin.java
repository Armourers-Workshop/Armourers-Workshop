package moe.plushie.armourers_workshop.compat.mixin.patch.render;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractRenderState;
import moe.plushie.armourers_workshop.compat.client.event.AbstractRenderLivingEntityEvent;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.init.event.client.RenderLivingEntityEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.16, 1.22)")
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "HEAD"))
    private void aw2$renderSetup(T entity, float f, float g, PoseStack poseStack, MultiBufferSource buffers, int i, CallbackInfo ci) {
        AbstractRenderState.createAndExtract(entity, f, g);
        EventManager.post(RenderLivingEntityEvent.Setup.class, AbstractRenderLivingEntityEvent.setup(entity, g, LivingEntityRenderer.class.cast(this)));
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V", shift = At.Shift.AFTER))
    private void aw2$renderPre(T entity, float f, float g, PoseStack poseStack, MultiBufferSource buffers, int i, CallbackInfo ci) {
        EventManager.post(RenderLivingEntityEvent.Pre.class, AbstractRenderLivingEntityEvent.pre(entity, i, OverlayTexture.NO_OVERLAY, poseStack, buffers, LivingEntityRenderer.class.cast(this)));
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "RETURN"))
    private void aw2$renderPost(T entity, float f, float g, PoseStack poseStack, MultiBufferSource buffers, int i, CallbackInfo ci) {
        EventManager.post(RenderLivingEntityEvent.Post.class, AbstractRenderLivingEntityEvent.post(entity, i, OverlayTexture.NO_OVERLAY, poseStack, buffers, LivingEntityRenderer.class.cast(this)));
    }
}
