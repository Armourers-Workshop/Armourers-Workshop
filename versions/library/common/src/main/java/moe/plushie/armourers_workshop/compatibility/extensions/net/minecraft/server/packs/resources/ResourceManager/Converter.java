package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.server.packs.resources.ResourceManager;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.20, )")
@Extension
public class Converter {

    public static ResourceManager asBundleManager(@This ResourceManager resourceManager) {
        ArrayList<PackResources> resources = new ArrayList<>();
        resourceManager.listPacks().forEach(it -> {
            // bundle data only contain data pack on mods.
            if (it.isBuiltin() || (!(it instanceof FilePackResources) && !(it instanceof PathPackResources))) {
                resources.add(it);
            }
        });
        return new MultiPackResourceManager(PackType.SERVER_DATA, resources);
    }
}
