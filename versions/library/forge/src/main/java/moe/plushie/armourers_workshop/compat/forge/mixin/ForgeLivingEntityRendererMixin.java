package moe.plushie.armourers_workshop.compat.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.renderer.state.AbstractRenderState;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeInitializer;
import moe.plushie.armourers_workshop.compat.forge.event.client.AbstractForgeRenderLivingEvent;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.16, 1.22)")
@Mixin(LivingEntityRenderer.class)
public abstract class ForgeLivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "HEAD"))
    private void aw2$renderPre(T entity, float f, float g, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        AbstractRenderState.createAndExtract(entity, f, g);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V", shift = At.Shift.AFTER))
    private void aw2$render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        AbstractForgeInitializer.getEventBus().post(new AbstractForgeRenderLivingEvent.Apply<>(entity, Objects.unsafeCast(this), f, poseStack, buffers, light));
    }
}
