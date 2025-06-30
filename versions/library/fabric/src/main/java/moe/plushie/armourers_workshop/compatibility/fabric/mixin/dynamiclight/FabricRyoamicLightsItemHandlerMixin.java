package moe.plushie.armourers_workshop.compatibility.fabric.mixin.dynamiclight;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.client.ClientDynamicLightHandler;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.thinkingstudio.ryoamiclights.api.item.ItemLightSources;

@Available("[1.16, )")
@Pseudo
@Mixin(ItemLightSources.class)
public class FabricRyoamicLightsItemHandlerMixin {

    @Inject(method = "getLuminance", at = @At("HEAD"), cancellable = true, remap = false)
    private static void aw2$getLuminance(ItemStack stack, boolean submergedInWater, CallbackInfoReturnable<Integer> cir) {
        ClientDynamicLightHandler.apply(stack, submergedInWater, cir);
    }
}
