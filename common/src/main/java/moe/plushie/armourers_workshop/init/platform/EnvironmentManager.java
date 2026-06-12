package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.compat.client.AbstractClientResourceManager;
import moe.plushie.armourers_workshop.compat.client.utils.AbstractGameProfile;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.OpenDistributionType;
import moe.plushie.armourers_workshop.core.utils.OpenGameProfile;
import moe.plushie.armourers_workshop.core.utils.OpenResourceManager;
import moe.plushie.armourers_workshop.core.utils.Version;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.Executor;

public class EnvironmentManager {

    public static File getRootDirectory() {
        return new File(Platform.get().gameDir().toFile(), "armourers_workshop");
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
        return Platform.get().currentServer();
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
        return Platform.get().environmentType() == EnvironmentType.SERVER;
    }

    @Nullable
    public static Version getModVersion(String modId) {
        return VersionResolver.getVersion(modId).orElse(null);
    }


    public static OpenGameProfile getClientUser() {
        return EnvironmentExecutor.callOnClient(() -> () -> AbstractGameProfile.wrap(Minecraft.getInstance().getUser().getGameProfile())).orElse(null);
    }

    public static Executor getClientExecutor() {
        return EnvironmentExecutor.callOnClient(() -> () -> Minecraft.getInstance()).orElse(null);
    }

    public static OpenResourceManager getClientResourceManager() {
        return EnvironmentExecutor.callOnClient(() -> AbstractClientResourceManager::getInstance).orElse(null);
    }
}


