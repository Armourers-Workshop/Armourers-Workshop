package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compat.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeEnvironment;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeRegistryManager;
import moe.plushie.armourers_workshop.init.environment.EnvironmentPlatformType;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.PlatformManager;
import moe.plushie.armourers_workshop.init.platform.forge.builder.ConfigBuilderImpl;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class PlatformManagerImpl extends PlatformManager {

    private static MinecraftServer CURRENT_SERVER;

    public static void attach(MinecraftServer server) {
        CURRENT_SERVER = server;
    }

    public static void detach(MinecraftServer server) {
        CURRENT_SERVER = null;
    }

    @Override
    public String getVersion(String modId) {
        var fileInfo = AbstractForgeEnvironment.getModFileById(modId);
        if (fileInfo != null && !fileInfo.getMods().isEmpty()) {
            var version = fileInfo.getMods().get(0).getVersion();
            return version.toString();
        }
        return null;
    }

    @Override
    public EnvironmentPlatformType getPlatformType() {
        return EnvironmentPlatformType.FORGE;
    }

    @Override
    public EnvironmentType getEnvironmentType() {
        if (AbstractForgeEnvironment.getDist().isDedicatedServer()) {
            return EnvironmentType.SERVER;
        }
        return EnvironmentType.CLIENT;
    }

    @Override
    public MinecraftServer getServer() {
        return CURRENT_SERVER;
    }

    @Override
    public Path getGameDir() {
        return AbstractForgeEnvironment.getGameDir();
    }

    @Override
    public boolean isDevelopment() {
        return !AbstractForgeEnvironment.isProduction();
    }

    @Override
    public IConfigSpec getClientConfig() {
        return ConfigBuilderImpl.createClientSpec();
    }

    @Override
    public IConfigSpec getCommonConfig() {
        return ConfigBuilderImpl.createCommonSpec();
    }

    @Override
    public AbstractRegistryManager getRegistryManager() {
        return AbstractForgeRegistryManager.INSTANCE;
    }
}
