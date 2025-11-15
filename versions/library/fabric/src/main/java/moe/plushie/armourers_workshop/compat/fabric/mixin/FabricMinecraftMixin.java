package moe.plushie.armourers_workshop.compat.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.platform.fabric.event.ClientStartupEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.16, )")
@Mixin(Minecraft.class)
public class FabricMinecraftMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;resizeDisplay()V"))
    private void aw2$didInit(GameConfig gameConfig, CallbackInfo ci) {
        Minecraft minecraft = Objects.unsafeCast(this);
        ClientStartupEvents.CLIENT_WILL_START.invoker().onClientWillStart(minecraft);
    }
}
