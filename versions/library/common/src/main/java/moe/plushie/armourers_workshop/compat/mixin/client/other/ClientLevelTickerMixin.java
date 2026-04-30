package moe.plushie.armourers_workshop.compat.mixin.client.other;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[16, )")
@Mixin(ClientLevel.class)
public class ClientLevelTickerMixin {

    @Inject(method = "tickNonPassenger", at = @At("HEAD"))
    private void aw2$tickNonPassengerPre(Entity entity, CallbackInfo ci) {
        ClientWardrobeHandler.willTick(entity);
    }

    @Inject(method = "tickNonPassenger", at = @At("TAIL"))
    private void aw2$tickNonPassengerPost(Entity entity, CallbackInfo ci) {
        ClientWardrobeHandler.didTick(entity);
    }
}
