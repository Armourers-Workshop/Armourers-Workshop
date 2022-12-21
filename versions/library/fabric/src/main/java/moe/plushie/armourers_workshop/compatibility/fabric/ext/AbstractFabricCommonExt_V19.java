package moe.plushie.armourers_workshop.compatibility.fabric.ext;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricCommonNativeProvider;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

@Available("[1.19, )")
public interface AbstractFabricCommonExt_V19 extends AbstractFabricCommonNativeProvider {

    @Override
    default void willPlayerDrop(Consumer<Player> consumer) {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, damageAmount) -> {
            if (entity instanceof Player) {
                consumer.accept((Player) entity);
            }
            return true;
        });
    }
}
