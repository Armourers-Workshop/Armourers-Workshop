package moe.plushie.armourers_workshop.init.mixin.forge;

import moe.plushie.armourers_workshop.init.platform.ReplayManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.replaymod.replay.ReplayHandler")
public class ForgeReplayMixin {

    @Inject(method = "<init>*", at = @At("RETURN"), remap = false)
    public void aw2$startReplay(CallbackInfo ci) {
        ReplayManager.startReplay();
    }

    @Inject(method = "endReplay", at = @At("HEAD"), remap = false)
    public void aw2$stopReplay(CallbackInfo ci) {
        ReplayManager.stopReplay();
    }
}
