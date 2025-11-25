package moe.plushie.armourers_workshop.compat.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IRenderAttachable;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Available("[1.16, )")
@Mixin(RenderStateShard.class)
public class RenderTypeMixin implements IRenderAttachable {

    @Unique
    private Runnable aw2$attachment;

    @Override
    public void attachRenderTask(Supplier<Runnable> provider) {
        if (aw2$attachment == null) {
            aw2$attachment = provider.get();
        }
    }

    @Inject(method = "clearRenderState", at = @At("RETURN"))
    public void aw2$loadCallback(CallbackInfo ci) {
        if (aw2$attachment != null) {
            aw2$attachment.run();
            aw2$attachment = null;
        }
    }
}
