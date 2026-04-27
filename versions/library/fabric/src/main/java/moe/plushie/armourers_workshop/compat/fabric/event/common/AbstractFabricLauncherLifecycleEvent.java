package moe.plushie.armourers_workshop.compat.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.event.common.LauncherClientSetupEvent;
import moe.plushie.armourers_workshop.init.event.common.LauncherCommonSetupEvent;
import moe.plushie.armourers_workshop.init.event.common.LauncherLoadCompleteEvent;

@Available("[16, )")
public class AbstractFabricLauncherLifecycleEvent {

    public static IEventHandler<LauncherClientSetupEvent> clientSetupFactory() {
        return (priority, receiveCancelled, subscriber) -> subscriber.accept(Runnable::run);
    }

    public static IEventHandler<LauncherCommonSetupEvent> commonSetupFactory() {
        return (priority, receiveCancelled, subscriber) -> subscriber.accept(Runnable::run);
    }

    public static IEventHandler<LauncherLoadCompleteEvent> loadCompleteFactory() {
        return (priority, receiveCancelled, subscriber) -> subscriber.accept(Runnable::run);
    }


}
