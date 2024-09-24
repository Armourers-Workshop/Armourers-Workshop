package moe.plushie.armourers_workshop.compatibility.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherClientSetupEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherCommonSetupEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherLoadCompleteEvent;

@Available("[1.16, )")
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
