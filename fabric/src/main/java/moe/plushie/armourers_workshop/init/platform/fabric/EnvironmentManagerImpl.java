package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ConfigBuilderImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.util.Optional;

@SuppressWarnings("unused")
public class EnvironmentManagerImpl {

    private static MinecraftServer CURRENT_SERVER;

    public static String getVersion() {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(ModConstants.MOD_ID);
        if (container.isPresent()) {
            return container.get().getMetadata().getVersion().toString();
        }
        return "Unknown";
    }

    public static EnvironmentType getEnvironmentType() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            return EnvironmentType.SERVER;
        }
        return EnvironmentType.CLIENT;
    }

    public static File getRootDirectory() {
        return new File(FabricLoader.getInstance().getGameDir().toFile(), "armourers_workshop");
    }

    public static MinecraftServer getServer() {
        return CURRENT_SERVER;
    }

    public static boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static IConfigSpec getClientConfigSpec() {
        return ConfigBuilderImpl.createClientSpec();
    }

    public static IConfigSpec getCommonConfigSpec() {
        return ConfigBuilderImpl.createCommonSpec();
    }

    public static void attach(MinecraftServer server) {
        CURRENT_SERVER = server;
    }

    public static void detach(MinecraftServer server) {
        CURRENT_SERVER = null;
    }

    public static boolean isInstalled(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
