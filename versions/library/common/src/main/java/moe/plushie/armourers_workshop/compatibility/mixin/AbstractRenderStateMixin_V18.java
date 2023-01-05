package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IRenderAttachable;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.18, )")
@Mixin(RenderStateShard.class)
public class AbstractRenderStateMixin_V18 implements IRenderAttachable {

    private Runnable aw$attachment;

    @Override
    public void attachRenderTask(Runnable runnable) {
        aw$attachment = runnable;
    }

    @Inject(method = "clearRenderState", at = @At("RETURN"))
    public void aw2$loadCallback(CallbackInfo ci) {
        if (aw$attachment != null) {
            aw$attachment.run();
            aw$attachment = null;
        }
    }
}
