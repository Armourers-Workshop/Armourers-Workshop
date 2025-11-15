package moe.plushie.armourers_workshop.compat.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.client.ClientDynamicLightHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Available("[1.16, )")
@Pseudo
@Mixin(targets = "net.optifine.DynamicLights")
public class DynamicLightsOptifineMixin {

    //public static int getLightLevel(Entity entity)
    //public static int getLightLevel(ItemStack itemStack);
    //public static double getLightLevel(BlockPos pos)

    @Inject(method = "getLightLevel(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private static void aw2$getLightLevel(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        var lightSource = ClientDynamicLightHandler.getLightSource(itemStack, false);
        if (lightSource != null) {
            cir.setReturnValue(lightSource.get(0));
        }
    }

    @Inject(method = "getLightLevel(Lnet/minecraft/world/entity/Entity;)I", at = @At(value = "INVOKE", target = "Lnet/optifine/DynamicLights;getLightLevel(Lnet/minecraft/world/item/ItemStack;)I"))
    private static void aw2$getLightLevelPre(Entity entity, CallbackInfoReturnable<Integer> cir) {
        ClientDynamicLightHandler.startTick(entity);
    }

    @Inject(method = "getLightLevel(Lnet/minecraft/world/entity/Entity;)I", at = @At(value = "INVOKE", target = "Lnet/optifine/DynamicLights;getLightLevel(Lnet/minecraft/world/item/ItemStack;)I", shift = At.Shift.AFTER))
    private static void aw2$getLightLevelPost(Entity entity, CallbackInfoReturnable<Integer> cir) {
        ClientDynamicLightHandler.endTick(entity);
    }

    @Inject(method = "getLightLevel(Lnet/minecraft/world/entity/Entity;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isOnFire()Z"), cancellable = true)
    private static void aw2$getLightLevel(Entity entity, CallbackInfoReturnable<Integer> cir) {
        var lightSource = ClientDynamicLightHandler.getLightSource(entity);
        if (lightSource != null) {
            cir.setReturnValue(lightSource.get(0));
        }
    }

    static {
        ClientDynamicLightHandler.init();
    }
}


