package moe.plushie.armourers_workshop.compat.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.event.common.DataPackEvent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

@Available("[16, )")
public class AbstractFabricDataPackEvent {

    public static IEventHandler<DataPackEvent.Sync> syncFactory() {
        // JOIN before the on data sync event, so we use join event.
        return (priority, receiveCancelled, subscriber) -> ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> subscriber.accept(() -> handler.player)));
    }
}
