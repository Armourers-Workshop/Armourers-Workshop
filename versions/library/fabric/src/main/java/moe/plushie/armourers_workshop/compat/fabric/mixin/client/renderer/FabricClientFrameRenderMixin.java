package moe.plushie.armourers_workshop.compat.fabric.mixin.client.renderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.event.AbstractRenderFrameEvent;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[21, )")
@Mixin(GameRenderer.class)
public class FabricClientFrameRenderMixin {

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void aw2$renderPre(DeltaTracker timer, boolean bl, CallbackInfo ci) {
        EventManager.post(RenderFrameEvent.Pre.class, AbstractRenderFrameEvent.pre(timer));
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void aw2$renderPost(DeltaTracker timer, boolean bl, CallbackInfo ci) {
        EventManager.post(RenderFrameEvent.Post.class, AbstractRenderFrameEvent.post(timer));
    }
}
