package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModFileInfo;

import java.nio.file.Path;

@Available("[1.21, 1.22)")
public class AbstractForgeEnvironment {

    public static IModFileInfo getModFileById(String modId) {
        return ModList.get().getModFileById(modId);
    }

    public static Dist getDist() {
        return FMLEnvironment.dist;
    }

    public static Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    public static boolean isProduction() {
        return FMLEnvironment.production;
    }
}
