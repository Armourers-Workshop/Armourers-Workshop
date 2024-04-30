package moe.plushie.armourers_workshop.compatibility.forge.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeConfig;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherConfigSetupEvent;

@Available("[1.16, )")
public class AbstractForgeConfigEvent {

    public static IEventHandler<LauncherConfigSetupEvent> registryFactory() {
        return AbstractForgeCommonEventsImpl.FML_CONFIG.map(event -> () -> new AbstractForgeConfig.Spec(event.getConfig().getSpec()));
    }
}
