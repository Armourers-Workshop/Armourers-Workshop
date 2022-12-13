package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlStateManager.class)
public class GlStateManagerMixin {

    @Inject(method = "_drawElements", at = @At("RETURN"), require = 0)
    private static void aw$drawElements(int i, int j, int k, long l, CallbackInfo ci) {
        SkinRenderExecutor.resume();
    }
}
