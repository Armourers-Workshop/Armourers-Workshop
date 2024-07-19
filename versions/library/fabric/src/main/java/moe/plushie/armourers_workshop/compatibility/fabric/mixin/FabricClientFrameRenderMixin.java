package moe.plushie.armourers_workshop.compatibility.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.platform.fabric.event.ClientFrameRenderEvents;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.16, )")
@Mixin(Minecraft.class)
public class FabricClientFrameRenderMixin {

    @Inject(method = "runTick", at = @At("HEAD"))
    private void aw2$renderPre(boolean bl, CallbackInfo ci) {
        ClientFrameRenderEvents.START.invoker().onStart(Minecraft.getInstance());
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    private void aw2$renderPost(boolean bl, CallbackInfo ci) {
        ClientFrameRenderEvents.END.invoker().onEnd(Minecraft.getInstance());
    }
}
