package moe.plushie.armourers_workshop.compat.forge.mixin.dynamiclight;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Conditional;
import moe.plushie.armourers_workshop.init.client.ClientDynamicLightHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Available("[1.20, 1.26)")
public class ForgeLambDynamicLightsMixin {

    @Conditional("lambdynlights")
    @Pseudo
    @Mixin(targets = "dev.lambdaurora.lambdynlights.resource.item.ItemLightSources")
    public static class ItemLightHandler {

        @Inject(method = "getLuminance", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
        private void aw2$getLuminance(ItemStack itemStack, boolean submergedInWater, CallbackInfoReturnable<Integer> cir) {
            var lightSource = ClientDynamicLightHandler.getLightSource(itemStack, submergedInWater);
            if (lightSource != null) {
                cir.setReturnValue(lightSource.get(cir.getReturnValue()));
            }
        }
    }

    @Conditional("lambdynlights")
    @Pseudo
    @Mixin(targets = "dev.lambdaurora.lambdynlights.resource.entity.EntityLightSources")
    public static class EntityLightHandler {

        @Inject(method = "getLuminance", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
        private void aw2$getLuminance(Entity entity, CallbackInfoReturnable<Integer> cir) {
            var lightSource = ClientDynamicLightHandler.getLightSource(entity);
            if (lightSource != null) {
                cir.setReturnValue(lightSource.get(cir.getReturnValue()));
            }
        }

        static {
            ClientDynamicLightHandler.init();
        }
    }

    @Conditional("lambdynlights")
    @Pseudo
    @Mixin(targets = "dev.lambdaurora.lambdynlights.resource.entity.BlockEntityLightSources")
    public static class BlockEntityLightHandler {

        @Inject(method = "getLuminance", at = @At("RETURN"), cancellable = true, remap = false, require = 0)
        private void aw2$getLuminance(BlockEntity entity, CallbackInfoReturnable<Integer> cir) {
            var lightSource = ClientDynamicLightHandler.getLightSource(entity);
            if (lightSource != null) {
                cir.setReturnValue(lightSource.get(cir.getReturnValue()));
            }
        }
    }
}
