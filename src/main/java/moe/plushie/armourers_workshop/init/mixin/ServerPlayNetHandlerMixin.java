package moe.plushie.armourers_workshop.init.mixin;


import moe.plushie.armourers_workshop.init.common.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.ServerPlayNetHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayNetHandler.class)
public class ServerPlayNetHandlerMixin {

    @Inject(method = "noBlocksAround", at = @At("RETURN"), cancellable = true)
    private void hooked_noBlocksAround(Entity entity, CallbackInfoReturnable<Boolean> callback) {
        if (callback.getReturnValue()) {
            callback.setReturnValue(ModEntities.noBlockEntitiesAround(entity));
        }
    }
}
