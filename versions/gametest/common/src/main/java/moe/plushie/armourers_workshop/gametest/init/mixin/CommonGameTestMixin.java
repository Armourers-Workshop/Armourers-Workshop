package moe.plushie.armourers_workshop.gametest.init.mixin;

import moe.plushie.armourers_workshop.gametest.init.Launcher;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class CommonGameTestMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void aw2$init(CallbackInfo ci) {
        Launcher.init();
    }
}
