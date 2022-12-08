package moe.plushie.armourers_workshop.init.platform.forge.addon;

import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.ToIntFunction;

public class BukkitAddon {

    public static void init() {
    }

    public static void register(String category, ResourceLocation registryName, ToIntFunction<ResourceLocation> provider) {
        EnvironmentExecutor.didSetup(EnvironmentType.COMMON, () -> () -> {
            ModLog.debug("{} => {} => {}", category, registryName, provider.applyAsInt(registryName));
        });
    }
}
