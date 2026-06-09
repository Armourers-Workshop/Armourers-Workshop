package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.data.AbstractPackResources;
import moe.plushie.armourers_workshop.core.utils.OpenResourceManager;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;

@Available("[18, )")
public class AbstractBundleResourceManager {

    public static OpenResourceManager wrap(OpenResourceManager resourceManager) {
        return AbstractResourceManager.wrap(remake(AbstractResourceManager.unwrap(resourceManager)));
    }

    private static ResourceManager remake(ResourceManager resourceManager) {
        var resources = new ArrayList<PackResources>();
        resourceManager.listPacks().forEach(it -> {
            // bundle data only contain data pack on mods.
            if (AbstractPackResources.isModResources(it)) {
                resources.add(it);
            }
        });
        return new MultiPackResourceManager(PackType.SERVER_DATA, resources);
    }
}
