package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.api.core.IResourceManager;
import moe.plushie.armourers_workshop.compatibility.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.OpenDistributionType;
import moe.plushie.armourers_workshop.init.environment.EnvironmentPlatformType;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;

public class EnvironmentManager {

    @ExpectPlatform
    public static String getModVersion(String modId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static EnvironmentPlatformType getPlatformType() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static EnvironmentType getEnvironmentType() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static File getRootDirectory() {
        throw new AssertionError();
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

    @ExpectPlatform
    public static MinecraftServer getServer() {
        throw new AssertionError();
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

    @Environment(EnvType.CLIENT)
    public static Minecraft getClient() {
        return Minecraft.getInstance();
    }

    @Environment(EnvType.CLIENT)
    public static Player getPlayer() {
        return getClient().player;
    }

    @Environment(EnvType.CLIENT)
    public static IResourceManager getResourceManager() {
        return getClient().getResourceManager().asResourceManager();
    }

    public static boolean isDedicatedServer() {
        return getEnvironmentType() == EnvironmentType.SERVER;
    }

    @ExpectPlatform
    public static boolean isDevelopment() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isInstalled(String modId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static IConfigSpec getClientConfigSpec() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static IConfigSpec getCommonConfigSpec() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static AbstractRegistryManager getRegistryManager() {
        throw new AssertionError();
    }
}


