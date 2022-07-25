package moe.plushie.armourers_workshop.init.mixin;


import moe.plushie.armourers_workshop.init.ModEntities;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPacketListenerMixin {

    @Inject(method = "noBlocksAround", at = @At("RETURN"), cancellable = true)
    private void hooked_noBlocksAround(Entity entity, CallbackInfoReturnable<Boolean> callback) {
        if (callback.getReturnValue()) {
            callback.setReturnValue(ModEntities.noBlockEntitiesAround(entity));
        }
    }
}