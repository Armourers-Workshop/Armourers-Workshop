package moe.plushie.armourers_workshop.compat.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractRenderPipeline;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.16, )")
@Mixin(RenderStateShard.class)
public class RenderTypeMixin {

    @Inject(method = "setupRenderState", at = @At("HEAD"))
    public void aw2$setupRenderState(CallbackInfo ci) {
        AbstractRenderPipeline.setupRenderState(this);
    }

    @Inject(method = "clearRenderState", at = @At("TAIL"))
    public void aw2$clearRenderState(CallbackInfo ci) {
        AbstractRenderPipeline.clearRenderState(this);
    }
}
