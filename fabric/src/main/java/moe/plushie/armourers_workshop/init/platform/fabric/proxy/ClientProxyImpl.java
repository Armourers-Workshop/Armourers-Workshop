package moe.plushie.armourers_workshop.init.platform.fabric.proxy;

import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfig;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigTracker;
import moe.plushie.armourers_workshop.init.platform.fabric.event.ClientStartupEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ClientProxyImpl implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT);

        // load all configs
        FabricConfigTracker.INSTANCE.loadConfigs(FabricConfig.Type.CLIENT, FabricLoader.getInstance().getConfigDir());

        ClientStartupEvents.CLIENT_WILL_START.register(minecraft -> {
            // we will call `ResourceManager.registerReloadListener` in willSetup phase.
            EnvironmentExecutor.willSetup(EnvironmentType.CLIENT);
        });

        ClientStartupEvents.CLIENT_STARTED.register(minecraft -> {
            EnvironmentExecutor.didInit(EnvironmentType.CLIENT);
            EnvironmentExecutor.didSetup(EnvironmentType.CLIENT);
        });
    }
}
