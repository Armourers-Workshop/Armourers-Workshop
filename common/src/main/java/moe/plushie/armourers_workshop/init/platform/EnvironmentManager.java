package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.init.environment.EnvironmentPlatformType;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.io.File;

public class EnvironmentManager {

    @ExpectPlatform
    public static String getVersion() {
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
        return getServer().getWorldPath(Constants.Folder.LOCAL_DB).toFile();
    }

    @ExpectPlatform
    public static MinecraftServer getServer() {
        throw new AssertionError();
    }

    @Environment(EnvType.CLIENT)
    public static Player getPlayer() {
        return Minecraft.getInstance().player;
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
}


