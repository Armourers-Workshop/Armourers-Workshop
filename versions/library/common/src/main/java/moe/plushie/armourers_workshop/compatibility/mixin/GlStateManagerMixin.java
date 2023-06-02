package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.20, )")
@Mixin(GlStateManager.class)
public class GlStateManagerMixin {

    @Inject(method = "_drawElements", at = @At("RETURN"), remap = false)
    private static void aw$drawElements(int i, int j, int k, long l, CallbackInfo ci) {
        SkinRenderExecutor.resume();
    }
}
