package moe.plushie.armourers_workshop.compat.fabric.core.event;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.event.common.BlockEvent;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

@Available("[16, )")
public class AbstractFabricBlockEvent {

    public static IEventHandler<BlockEvent.Destroy> destroyFactory() {
        return (priority, receiveCancelled, subscriber) -> PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            subscriber.accept(AbstractFabricBlockEventImpl.destroy(level, player, pos, state, blockEntity));
            return true;
        });
    }
}
