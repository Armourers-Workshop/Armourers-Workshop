package moe.plushie.armourers_workshop.compatibility.forge.event.common;

import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherClientSetupEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherCommonSetupEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherLoadCompleteEvent;

public class AbstractForgeLauncherLifecycleEvent {

    public static IEventHandler<LauncherClientSetupEvent> clientSetupFactory() {
        return AbstractForgeCommonEventsImpl.FML_CLIENT_SETUP.map(event -> event::enqueueWork);
    }

    public static IEventHandler<LauncherCommonSetupEvent> commonSetupFactory() {
        return AbstractForgeCommonEventsImpl.FML_COMMON_SETUP.map(event -> event::enqueueWork);
    }

    public static IEventHandler<LauncherLoadCompleteEvent> loadCompleteFactory() {
        return AbstractForgeCommonEventsImpl.FML_LOAD_COMPLETE.map(event -> event::enqueueWork);
    }

}
