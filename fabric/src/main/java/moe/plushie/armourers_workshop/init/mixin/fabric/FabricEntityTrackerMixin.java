package moe.plushie.armourers_workshop.init.mixin.fabric;

import moe.plushie.armourers_workshop.init.platform.fabric.event.EntityLifecycleEvents;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public class FabricEntityTrackerMixin {

    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "addPairing", at = @At("TAIL"))
    private void aw2$startTracking(ServerPlayer player, CallbackInfo ci) {
        // the fabric start tracking event is too early, it will callback before the vanilla handler,
        // so we need a new start tracking callback after the vanilla handler completed.
        EntityLifecycleEvents.DID_START_TRACKING.invoker().onStartTracking(this.entity, player);
    }
}
