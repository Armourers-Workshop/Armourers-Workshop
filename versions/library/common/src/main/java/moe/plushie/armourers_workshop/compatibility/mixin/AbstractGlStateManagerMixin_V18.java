package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.18, )")
@Mixin(GlStateManager.class)
public class AbstractGlStateManagerMixin_V18 {

    @Inject(method = "_drawElements", at = @At("RETURN"))
    private static void aw$drawElements(int i, int j, int k, long l, CallbackInfo ci) {
        SkinRenderExecutor.resume();
    }
}
