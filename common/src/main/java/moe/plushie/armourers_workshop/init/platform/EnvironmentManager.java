package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compat.client.AbstractClientResourceManager;
import moe.plushie.armourers_workshop.compat.client.AbstractGameProfile;
import moe.plushie.armourers_workshop.compat.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.compat.core.AbstractResourceManager;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.OpenDistributionType;
import moe.plushie.armourers_workshop.core.utils.OpenGameProfile;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentPlatformType;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.Executor;

public class EnvironmentManager {

    private static final PlatformManager PLATFORM = PlatformLoader.load(PlatformManager.class);

    public static EnvironmentPlatformType getPlatformType() {
        return PLATFORM.getPlatformType();
    }

    public static EnvironmentType getEnvironmentType() {
        return PLATFORM.getEnvironmentType();
    }

    public static File getRootDirectory() {
        return new File(PLATFORM.getGameDir().toFile(), "armourers_workshop");
    }

    public static File getSkinLibraryDirectory() {
        return new File(getRootDirectory(), "skin-library");
    }

    public static File getSkinCacheDirectory() {
        return new File(getRootDirectory(), "skin-cache");
    }

    public static File getSkinDatabaseDirectory() {
        return getServer().getWorldPath(new LevelResource(Constants.Folder.LOCAL_DB)).toFile();
    }

    public static MinecraftServer getServer() {
        return PLATFORM.getServer();
    }

    public static OpenDistributionType getDistributionType(MinecraftServer server) {
        if (server == null) {
            return OpenDistributionType.CLIENT;
        }
        if (server.isDedicatedServer()) {
            return OpenDistributionType.DEDICATED_SERVER;
        }
        return OpenDistributionType.INTEGRATED_SERVER;
    }

    public static boolean isDedicatedServer() {
        return getEnvironmentType() == EnvironmentType.SERVER;
    }

    @Nullable
    public static String getModVersion(String modId) {
        return PLATFORM.getVersion(modId);
    }

    public static boolean isDevelopment() {
        return PLATFORM.isDevelopment();
    }

    public static IConfigSpec getClientConfigSpec() {
        return PLATFORM.getClientConfig();
    }

    public static IConfigSpec getCommonConfigSpec() {
        return PLATFORM.getCommonConfig();
    }

    public static AbstractRegistryManager getRegistryManager() {
        return PLATFORM.getRegistryManager();
    }

    public static OpenGameProfile getClientUser() {
        return EnvironmentExecutor.callOnClient(() -> () -> AbstractGameProfile.wrap(Minecraft.getInstance().getUser().getGameProfile())).orElse(null);
    }

    public static Executor getClientExecutor() {
        return EnvironmentExecutor.callOnClient(() -> () -> Minecraft.getInstance()).orElse(null);
    }

    public static AbstractResourceManager getClientResourceManager() {
        return EnvironmentExecutor.callOnClient(() -> AbstractClientResourceManager::getInstance).orElse(null);
    }
}


