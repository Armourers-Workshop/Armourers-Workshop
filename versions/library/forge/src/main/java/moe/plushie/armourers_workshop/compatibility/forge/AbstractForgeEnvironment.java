package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModFileInfo;

@Available("[1.21, )")
public class AbstractForgeEnvironment {

    public static final boolean production = FMLEnvironment.production;

    public static final Dist dist = FMLEnvironment.dist;

    public static final FMLPaths GAMEDIR = FMLPaths.GAMEDIR;

    public static IModFileInfo getModFileById(String modId) {
        return ModList.get().getModFileById(modId);
    }
}
