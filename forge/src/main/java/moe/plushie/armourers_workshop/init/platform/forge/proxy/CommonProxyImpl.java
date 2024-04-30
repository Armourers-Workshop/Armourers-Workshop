package moe.plushie.armourers_workshop.init.platform.forge.proxy;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherCommonSetupEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherLoadCompleteEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStartingEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.ServerStoppedEvent;
import moe.plushie.armourers_workshop.init.platform.forge.EnvironmentManagerImpl;

public class CommonProxyImpl {

    public static void init() {
        ArmourersWorkshop.init();

        // prioritize handle.
        EventManager.listen(ServerStartingEvent.class, event -> EnvironmentManagerImpl.attach(event.getServer()));
        EventManager.listen(ServerStoppedEvent.class, event -> EnvironmentManagerImpl.detach(event.getServer()));

        EnvironmentExecutor.willInit(EnvironmentType.COMMON);
        EnvironmentExecutor.willSetup(EnvironmentType.COMMON);

        // listen the fml events.
        EventManager.listen(LauncherCommonSetupEvent.class, event -> EnvironmentExecutor.didInit(EnvironmentType.COMMON));
        EventManager.listen(LauncherLoadCompleteEvent.class, event -> event.enqueueWork(() -> EnvironmentExecutor.didSetup(EnvironmentType.COMMON)));
    }
}
