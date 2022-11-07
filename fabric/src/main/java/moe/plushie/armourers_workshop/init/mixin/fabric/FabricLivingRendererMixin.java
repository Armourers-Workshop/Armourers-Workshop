package moe.plushie.armourers_workshop.init.mixin.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class FabricLivingRendererMixin<T extends LivingEntity> {

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "HEAD"))
    private void aw2$render_pre(T entity, float p_225623_2_, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderLivingPre(entity, p_225623_2_, partialTicks, light, matrixStack, buffers, LivingEntityRenderer.class.cast(this));
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V", shift = At.Shift.AFTER))
    private void aw2$render(T entity, float p_225623_2_, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderLiving(entity, p_225623_2_, partialTicks, light, matrixStack, buffers, LivingEntityRenderer.class.cast(this));
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "RETURN"))
    private void aw2$render_post(T entity, float p_225623_2_, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderLivingPost(entity, p_225623_2_, partialTicks, light, matrixStack, buffers, LivingEntityRenderer.class.cast(this));
    }
}
