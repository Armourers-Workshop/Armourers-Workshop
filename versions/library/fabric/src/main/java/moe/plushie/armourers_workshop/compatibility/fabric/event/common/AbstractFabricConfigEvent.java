package moe.plushie.armourers_workshop.compatibility.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherConfigSetupEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigEvents;

@Available("[1.16, )")
public class AbstractFabricConfigEvent {

    public static IEventHandler<LauncherConfigSetupEvent> registryFactory() {
        return (priority, receiveCancelled, subscriber) -> {
            FabricConfigEvents.LOADING.register(config -> subscriber.accept(config::getSpec));
            FabricConfigEvents.RELOADING.register(config -> subscriber.accept(config::getSpec));
        };
    }
}
