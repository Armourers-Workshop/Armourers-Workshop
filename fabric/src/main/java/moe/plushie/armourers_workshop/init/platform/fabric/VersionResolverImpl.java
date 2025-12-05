package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.init.platform.VersionResolver;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class VersionResolverImpl extends VersionResolver {

    @Nullable
    @Override
    protected String search(String modId) {
        var container = FabricLoader.getInstance().getModContainer(modId);
        return container.map(modContainer -> modContainer.getMetadata().getVersion().toString()).orElse(null);
    }
}
