package moe.plushie.armourers_workshop.init.mixin.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderSpecificArmEvents;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class FabricPlayerRendererMixin {

    @Inject(method = "renderRightHand", at = @At("HEAD"), cancellable = true)
    public void hooked_renderRightHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer player, CallbackInfo ci) {
        if (!RenderSpecificArmEvents.MAIN_HAND.invoker().render(poseStack, multiBufferSource, i, player, InteractionHand.MAIN_HAND)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
    public void hooked_renderLeftHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer player, CallbackInfo ci) {
        if (!RenderSpecificArmEvents.OFF_HAND.invoker().render(poseStack, multiBufferSource, i, player, InteractionHand.OFF_HAND)) {
            ci.cancel();
        }
    }

}
