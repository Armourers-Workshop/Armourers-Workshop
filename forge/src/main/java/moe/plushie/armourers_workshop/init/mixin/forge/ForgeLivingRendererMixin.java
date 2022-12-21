package moe.plushie.armourers_workshop.init.mixin.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class ForgeLivingRendererMixin<T extends LivingEntity> {

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V", shift = At.Shift.AFTER))
    private void aw2$render(T entity, float p_225623_2_, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffers, int light, CallbackInfo ci) {
        IPoseStack poseStack = AbstractPoseStack.wrap(poseStackIn);
        ClientWardrobeHandler.onRenderLiving(entity, partialTicks, light, poseStack, buffers, LivingEntityRenderer.class.cast(this));
    }
}
