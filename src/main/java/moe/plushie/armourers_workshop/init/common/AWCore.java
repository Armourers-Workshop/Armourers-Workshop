package moe.plushie.armourers_workshop.init.common;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import javax.annotation.Nullable;
import java.io.File;

public class AWCore {

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(getModId(), path);
    }

    public static String getModId() {
        return "armourers_workshop";
    }

    public static void init() {
    }

    public static ResourceLocation getItemIcon(ISkinType skinType) {
        if (skinType == SkinTypes.UNKNOWN || skinType.getRegistryName() == null) {
            return null;
        }
        return AWCore.resource("textures/item/template/" + skinType.getRegistryName().getPath() + ".png");
    }

    public static ResourceLocation getCustomModel(ResourceLocation resourceLocation) {
        String name = resourceLocation.getPath().toLowerCase();
        name = name.replaceAll("\\.base", "");
        name = name.replaceAll("\\.", "_");
        return AWCore.resource("skin/" + name);
    }

    public static File getRootDirectory() {
        return new File(FMLPaths.GAMEDIR.get().toFile(), "armourers_workshop");
    }

    public static File getSkinLibraryDirectory() {
        return new File(getRootDirectory(), "skin-library");
    }

    public static File getSkinCacheDirectory() {
        return new File(getRootDirectory(), "skin-cache");
    }


    @Nullable
    public static ArtifactVersion getVersion() {
        ModFileInfo fileInfo = ModList.get().getModFileById(AWCore.getModId());
        if (fileInfo != null && fileInfo.getMods().size() != 0) {
            return fileInfo.getMods().get(0).getVersion();
        }
        return null;
    }
}
