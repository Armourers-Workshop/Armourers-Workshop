package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compat.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.compat.fabric.AbstractFabricRegistryManager;
import moe.plushie.armourers_workshop.init.environment.EnvironmentPlatformType;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.PlatformManager;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ConfigBuilderImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
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
    public EnvironmentType getEnvironmentType() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            return EnvironmentType.SERVER;
        }
        return EnvironmentType.CLIENT;
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public MinecraftServer getServer() {
        return CURRENT_SERVER;
    }

    @Override
    public boolean isDevelopment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
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
    public EnvironmentPlatformType getPlatformType() {
        return EnvironmentPlatformType.FABRIC;
    }

    @Override
    public AbstractRegistryManager getRegistryManager() {
        return AbstractFabricRegistryManager.INSTANCE;
    }
}
