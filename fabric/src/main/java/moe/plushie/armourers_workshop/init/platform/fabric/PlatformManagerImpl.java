package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.PlatformManager;
import moe.plushie.armourers_workshop.init.platform.PlatformType;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ConfigBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.runtime.ClientPlatformImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.runtime.CommonPlatformImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class PlatformManagerImpl extends PlatformManager {

    public PlatformManagerImpl() {
        super(() -> CommonPlatformImpl::new, () -> ClientPlatformImpl::new);
    }

    @Override
    public PlatformType platformType() {
        return PlatformType.FABRIC;
    }

    @Override
    public EnvironmentType environmentType() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            return EnvironmentType.SERVER;
        }
        return EnvironmentType.CLIENT;
    }

    @Override
    public boolean isDevelopment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path gameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public ConfigBuilderImpl config() {
        return new ConfigBuilderImpl();
    }
}
