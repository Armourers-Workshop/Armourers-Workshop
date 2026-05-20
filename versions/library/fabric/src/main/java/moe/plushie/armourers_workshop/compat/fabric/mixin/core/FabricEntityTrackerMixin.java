package moe.plushie.armourers_workshop.compat.fabric.mixin.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.event.common.PlayerEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[16, )")
@Mixin(ServerEntity.class)
public class FabricEntityTrackerMixin {

    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "addPairing", at = @At("TAIL"))
    private void aw2$startTracking(ServerPlayer player, CallbackInfo ci) {
        // the fabric start tracking event is too early, it will callback before the vanilla handler,
        // so we need a new start tracking callback after the vanilla handler completed.
        EventManager.post(PlayerEvent.StartTracking.class, new PlayerEvent.StartTracking() {
            @Override
            public Entity target() {
                return entity;
            }

            @Override
            public ServerPlayer player() {
                return player;
            }
        });
    }
}
