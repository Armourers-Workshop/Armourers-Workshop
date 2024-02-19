package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeEnvironment;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.forge.builder.ConfigBuilderImpl;
import net.minecraft.server.MinecraftServer;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.io.File;

import manifold.ext.rt.api.auto;

@SuppressWarnings("unused")
public class EnvironmentManagerImpl {

    private static MinecraftServer CURRENT_SERVER;

    public static String getVersion() {
        auto fileInfo = AbstractForgeEnvironment.getModFileById(ModConstants.MOD_ID);
        if (fileInfo != null && fileInfo.getMods().size() != 0) {
            ArtifactVersion version = fileInfo.getMods().get(0).getVersion();
            return version.toString();
        }
        return "Unknown";
    }

    public static EnvironmentType getEnvironmentType() {
        if (AbstractForgeEnvironment.dist.isDedicatedServer()) {
            return EnvironmentType.SERVER;
        }
        return EnvironmentType.CLIENT;
    }

    public static MinecraftServer getServer() {
        return CURRENT_SERVER;
    }

    public static File getRootDirectory() {
        return new File(AbstractForgeEnvironment.GAMEDIR.get().toFile(), "armourers_workshop");
    }

    public static boolean isDevelopment() {
        return !AbstractForgeEnvironment.production;
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
        return AbstractForgeEnvironment.getModFileById(modId) != null;
    }
}
