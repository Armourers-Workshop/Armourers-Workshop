package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.compat.forge.AbstractForgeEnvironment;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.PlatformManager;
import moe.plushie.armourers_workshop.init.platform.PlatformType;
import moe.plushie.armourers_workshop.init.platform.forge.builder.ConfigBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.forge.runtime.ClientPlatformImpl;
import moe.plushie.armourers_workshop.init.platform.forge.runtime.CommonPlatformImpl;

import java.nio.file.Path;

public class PlatformManagerImpl extends PlatformManager {

    public PlatformManagerImpl() {
        super(() -> CommonPlatformImpl::new, () -> ClientPlatformImpl::new);
    }

    @Override
    public PlatformType platformType() {
        return PlatformType.FORGE;
    }

    @Override
    public EnvironmentType environmentType() {
        if (AbstractForgeEnvironment.getDist().isDedicatedServer()) {
            return EnvironmentType.SERVER;
        }
        return EnvironmentType.CLIENT;
    }

    @Override
    public boolean isDevelopment() {
        return !AbstractForgeEnvironment.isProduction();
    }

    @Override
    public Path gameDir() {
        return AbstractForgeEnvironment.getGameDir();
    }

    @Override
    public ConfigBuilderImpl config() {
        return new ConfigBuilderImpl();
    }
}
