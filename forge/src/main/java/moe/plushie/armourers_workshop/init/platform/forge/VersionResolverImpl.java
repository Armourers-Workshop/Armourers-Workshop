package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.compat.forge.AbstractForgeEnvironment;
import moe.plushie.armourers_workshop.init.platform.VersionResolver;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class VersionResolverImpl extends VersionResolver {

    @Nullable
    @Override
    protected String search(String modId) {
        var fileInfo = AbstractForgeEnvironment.getModFileById(modId);
        if (fileInfo != null && !fileInfo.getMods().isEmpty()) {
            var version = fileInfo.getMods().get(0).getVersion();
            return version.toString();
        }
        return null;
    }
}
