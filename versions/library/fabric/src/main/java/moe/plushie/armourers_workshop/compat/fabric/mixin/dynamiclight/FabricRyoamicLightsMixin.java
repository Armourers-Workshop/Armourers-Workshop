package moe.plushie.armourers_workshop.compat.fabric.mixin.dynamiclight;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.client.ClientDynamicLightHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.thinkingstudio.ryoamiclights.api.DynamicLightHandler;
import org.thinkingstudio.ryoamiclights.api.DynamicLightHandlers;
import org.thinkingstudio.ryoamiclights.api.item.ItemLightSources;

@Available("[1.16, )")
public class FabricRyoamicLightsMixin {

    @Pseudo
    @Mixin(ItemLightSources.class)
    public static class ItemLightHandler {

        @Inject(method = "getLuminance", at = @At("RETURN"), cancellable = true, require = 0)
        private static void aw2$getLuminance(ItemStack itemStack, boolean submergedInWater, CallbackInfoReturnable<Integer> cir) {
            var lightSource = ClientDynamicLightHandler.getLightSource(itemStack, submergedInWater);
            if (lightSource != null) {
                cir.setReturnValue(lightSource.get(cir.getReturnValue()));
            }
        }
    }

    @Pseudo
    @Mixin(DynamicLightHandlers.class)
    public static class EntityLightHandler {

        @Inject(method = "getDynamicLightHandler(Lnet/minecraft/world/entity/EntityType;)Lorg/thinkingstudio/ryoamiclights/api/DynamicLightHandler;", at = @At("RETURN"), cancellable = true, require = 0)
        private static <T extends Entity> void aw2$getDynamicLightHandler(EntityType<T> type, CallbackInfoReturnable<DynamicLightHandler<T>> cir) {
            var oldValue = cir.getReturnValue();
            cir.setReturnValue(new DynamicLightHandler<T>() {
                @Override
                public int getLuminance(T entity) {
                    var luminance = 0;
                    if (oldValue != null) {
                        luminance = oldValue.getLuminance(entity);
                    }
                    var lightSource = ClientDynamicLightHandler.getLightSource(entity);
                    if (lightSource != null) {
                        return lightSource.get(luminance);
                    }
                    return luminance;
                }

                @Override
                public boolean isWaterSensitive(T entity) {
                    if (oldValue != null) {
                        return oldValue.isWaterSensitive(entity);
                    }
                    return false;
                }
            });
        }

        static {
            ClientDynamicLightHandler.init();
        }
    }

    @Pseudo
    @Mixin(DynamicLightHandlers.class)
    public static class BlockEntityLightHandler {

        @Inject(method = "getDynamicLightHandler(Lnet/minecraft/world/level/block/entity/BlockEntityType;)Lorg/thinkingstudio/ryoamiclights/api/DynamicLightHandler;", at = @At("RETURN"), cancellable = true, require = 0)
        private static <T extends BlockEntity> void aw2$getDynamicLightHandler(BlockEntityType<T> type, CallbackInfoReturnable<DynamicLightHandler<T>> cir) {
            var oldValue = cir.getReturnValue();
            cir.setReturnValue(new DynamicLightHandler<T>() {
                @Override
                public int getLuminance(T entity) {
                    var luminance = 0;
                    if (oldValue != null) {
                        luminance = oldValue.getLuminance(entity);
                    }
                    var lightSource = ClientDynamicLightHandler.getLightSource(entity);
                    if (lightSource != null) {
                        return lightSource.get(luminance);
                    }
                    return luminance;
                }

                @Override
                public boolean isWaterSensitive(T entity) {
                    if (oldValue != null) {
                        return oldValue.isWaterSensitive(entity);
                    }
                    return false;
                }
            });
        }
    }
}
