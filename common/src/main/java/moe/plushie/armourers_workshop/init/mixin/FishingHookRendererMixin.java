package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import manifold.ext.rt.api.auto;

@Mixin(FishingHookRenderer.class)
public class FishingHookRendererMixin<T extends FishingHook> {

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", ordinal = 0, shift = At.Shift.AFTER))
    public void aw2$render(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource renderType, int light, CallbackInfo ci) {
        auto originStack = ClientWardrobeHandler.RENDERING_POSE_STACK;
        if (originStack != null) {
            IPoseStack resultStack = AbstractPoseStack.wrap(poseStack);
            resultStack.last().set(originStack.last());
        }
    }
}
