package moe.plushie.armourers_workshop.init.platform.forge.proxy;

import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherClientSetupEvent;
import moe.plushie.armourers_workshop.init.platform.event.common.LauncherLoadCompleteEvent;

public class ClientProxyImpl {

    public static void init() {
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT);
        EnvironmentExecutor.willSetup(EnvironmentType.CLIENT);

        // listen the fml events.
        EventManager.listen(LauncherClientSetupEvent.class, event -> EnvironmentExecutor.didInit(EnvironmentType.CLIENT));
        EventManager.listen(LauncherLoadCompleteEvent.class, event -> event.enqueueWork(() -> EnvironmentExecutor.didSetup(EnvironmentType.CLIENT)));
    }
}
