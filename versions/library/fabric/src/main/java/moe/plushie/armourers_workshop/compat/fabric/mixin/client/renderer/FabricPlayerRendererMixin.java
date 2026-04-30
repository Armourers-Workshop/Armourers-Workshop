package moe.plushie.armourers_workshop.compat.fabric.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderSpecificArmEvents;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[16, 26)")
@Mixin(PlayerRenderer.class)
public class FabricPlayerRendererMixin {

    @Inject(method = "renderRightHand", at = @At("HEAD"), cancellable = true)
    public void aw2$renderRightHand(PoseStack poseStack, MultiBufferSource bufferSource, int i, AbstractClientPlayer player, CallbackInfo ci) {
        if (!RenderSpecificArmEvents.ARM.invoker().render(player, i, OpenInteractionHand.MAIN_HAND, AbstractGraphicsRenderer.wrap(poseStack, bufferSource))) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
    public void aw2$renderLeftHand(PoseStack poseStack, MultiBufferSource bufferSource, int i, AbstractClientPlayer player, CallbackInfo ci) {
        if (!RenderSpecificArmEvents.ARM.invoker().render(player, i, OpenInteractionHand.OFF_HAND, AbstractGraphicsRenderer.wrap(poseStack, bufferSource))) {
            ci.cancel();
        }
    }
}
