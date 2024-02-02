package moe.plushie.armourers_workshop.init.mixin.fabric;

import moe.plushie.armourers_workshop.init.platform.fabric.event.ClientStartupEvents;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class FabricMinecraftMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;resizeDisplay()V"))
    private void aw$didInit(GameConfig gameConfig, CallbackInfo ci) {
        Minecraft minecraft = ObjectUtils.unsafeCast(this);
        ClientStartupEvents.CLIENT_WILL_START.invoker().onClientWillStart(minecraft);
    }
}
