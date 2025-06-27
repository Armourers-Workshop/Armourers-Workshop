package moe.plushie.armourers_workshop.compatibility.fabric.mixin.dynamiclight;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandler;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.client.ClientDynamicLightHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Available("[1.18, )")
@Pseudo
@Mixin(DynamicLightHandlers.class)
public class FabricLambDynamicLightsEntityHandlerMixin {

    @Redirect(method = "getLuminanceFrom(Lnet/minecraft/world/entity/Entity;)I", at = @At(value = "INVOKE", target = "Ldev/lambdaurora/lambdynlights/api/DynamicLightHandlers;getDynamicLightHandler(Lnet/minecraft/world/entity/EntityType;)Ldev/lambdaurora/lambdynlights/api/DynamicLightHandler;"))
    private static <T extends Entity> DynamicLightHandler<T> aw2$getLuminanceFromEntity(EntityType<T> type, T entity) {
        var handler = DynamicLightHandlers.getDynamicLightHandler(type);
        return ClientDynamicLightHandler.apply(handler, entity, data -> new DynamicLightHandler<T>() {
            @Override
            public int getLuminance(T source) {
                return data.getLuminance(source);
            }

            @Override
            public boolean isWaterSensitive(T source) {
                return data.isWaterSensitive(source);
            }
        });
    }

    @Redirect(method = "getLuminanceFrom(Lnet/minecraft/world/level/block/entity/BlockEntity;)I", at = @At(value = "INVOKE", target = "Ldev/lambdaurora/lambdynlights/api/DynamicLightHandlers;getDynamicLightHandler(Lnet/minecraft/world/level/block/entity/BlockEntityType;)Ldev/lambdaurora/lambdynlights/api/DynamicLightHandler;"))
    private static <T extends BlockEntity> DynamicLightHandler<T> aw2$getLuminanceFromBlockEntity(BlockEntityType<T> type, T entity) {
        var handler = DynamicLightHandlers.getDynamicLightHandler(type);
        return ClientDynamicLightHandler.apply(handler, entity, data -> new DynamicLightHandler<T>() {
            @Override
            public int getLuminance(T source) {
                return data.getLuminance(source);
            }

            @Override
            public boolean isWaterSensitive(T source) {
                return data.isWaterSensitive(source);
            }
        });
    }

    static {
        ClientDynamicLightHandler.init();
    }
}
