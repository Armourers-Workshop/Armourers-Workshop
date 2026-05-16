package moe.plushie.armourers_workshop.compat.fabric;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionResult;
import moe.plushie.armourers_workshop.init.platform.fabric.event.EntityLifecycleEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

@Available("[16, 26)")
public class AbstractFabricEventDispatcher {

    public static void init() {
        EntitySleepEvents.ALLOW_BED.register((entity, sleepingPos, state, vanillaResult) -> {
            var result = EntityLifecycleEvents.ALLOW_BED.invoker().allowBed(entity, sleepingPos, state, vanillaResult);
            return AbstractInteractionResult.unwrap(result);
        });

        EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
            EntityLifecycleEvents.STOP_SLEEPING.invoker().onStopSleeping(entity, sleepingPos);
        });

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            var result = EntityLifecycleEvents.USE_BLOCK.invoker().interact(player, level, hand, hitResult);
            return AbstractInteractionResult.unwrap(result);
        });

        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            var result = EntityLifecycleEvents.ATTACK_BLOCK.invoker().interact(player, level, hand, pos, direction);
            return AbstractInteractionResult.unwrap(result);
        });
    }
}
