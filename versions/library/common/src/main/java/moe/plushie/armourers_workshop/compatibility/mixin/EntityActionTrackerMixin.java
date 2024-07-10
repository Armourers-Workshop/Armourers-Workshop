package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.core.data.EntityActionSet;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityActionTrackerMixin {

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At("RETURN"))
    private void aw2$startRiding(Entity target, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        var entity = Entity.class.cast(this);
        var stateTracker = EntityActionSet.of(entity);
        if (stateTracker != null) {
            stateTracker.startRiding(target);
        }
    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    private void aw2$stopRiding(CallbackInfo ci) {
        var entity = Entity.class.cast(this);
        var stateTracker = EntityActionSet.of(entity);
        if (stateTracker != null) {
            stateTracker.stopRiding(entity.getVehicle());
        }
    }
}
