package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compat.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.init.environment.EnvironmentPlatformType;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

public abstract class PlatformManager {

    public abstract EnvironmentPlatformType getPlatformType();

    public abstract EnvironmentType getEnvironmentType();


    public abstract Path getGameDir();


    public abstract IConfigSpec getClientConfig();

    public abstract IConfigSpec getCommonConfig();


    public abstract MinecraftServer getServer();

    public abstract AbstractRegistryManager getRegistryManager();


    public abstract String getVersion(String modId);


    public abstract boolean isDevelopment();
}
