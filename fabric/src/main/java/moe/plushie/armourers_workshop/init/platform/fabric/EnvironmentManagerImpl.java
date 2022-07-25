package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.init.platform.environment.EnvironmentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.File;
import java.util.Optional;

@SuppressWarnings("unused")
public class EnvironmentManagerImpl {

    public static String getVersion() {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(ArmourersWorkshop.MOD_ID);
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

    public static boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
