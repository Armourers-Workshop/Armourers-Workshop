package moe.plushie.armourers_workshop.compat.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractDeltaTracker;
import moe.plushie.armourers_workshop.init.platform.fabric.event.ClientFrameRenderEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.21, )")
@Mixin(GameRenderer.class)
public class FabricClientFrameRenderMixin {

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void aw2$renderPre(DeltaTracker timer, boolean bl, CallbackInfo ci) {
        var minecraft = Minecraft.getInstance();
        var delta = new AbstractDeltaTracker(minecraft.level, timer, minecraft.isPaused());
        ClientFrameRenderEvents.START.invoker().onStart(delta);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void aw2$renderPost(DeltaTracker timer, boolean bl, CallbackInfo ci) {
        var minecraft = Minecraft.getInstance();
        var delta = new AbstractDeltaTracker(minecraft.level, timer, minecraft.isPaused());
        ClientFrameRenderEvents.END.invoker().onEnd(delta);
    }
}
