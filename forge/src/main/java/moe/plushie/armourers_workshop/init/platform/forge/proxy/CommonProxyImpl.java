package moe.plushie.armourers_workshop.init.platform.forge.proxy;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.event.common.LauncherCommonSetupEvent;
import moe.plushie.armourers_workshop.init.event.common.LauncherLoadCompleteEvent;

public class CommonProxyImpl {

    public static void init() {
        ArmourersWorkshop.init();

        EnvironmentExecutor.willInit(EnvironmentType.COMMON);
        EnvironmentExecutor.willSetup(EnvironmentType.COMMON);

        // listen the fml events.
        EventBus.register(LauncherCommonSetupEvent.class, event -> EnvironmentExecutor.didInit(EnvironmentType.COMMON));
        EventBus.register(LauncherLoadCompleteEvent.class, event -> event.enqueueWork(() -> EnvironmentExecutor.didSetup(EnvironmentType.COMMON)));
    }
}
