package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHookRenderer.class)
public class FishingHookRendererMixin<T extends FishingHook> {

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", ordinal = 1, shift = At.Shift.AFTER))
    public void aw2$render(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource renderType, int light, CallbackInfo ci) {
        CallbackInfo cir = new CallbackInfo("", true);
        ClientWardrobeHandler.onRenderFishingHook(entity, partialTicks, light, poseStack, renderType, cir);
        if (cir.isCancelled()) {
            poseStack.mulPoseMatrix(OpenMatrix4f.createScaleMatrix(0, 0, 0));
            poseStack.mulNormalMatrix(OpenMatrix3f.createScaleMatrix(0, 0, 0));
        }
    }
}
