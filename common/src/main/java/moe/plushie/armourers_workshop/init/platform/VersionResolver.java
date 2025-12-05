package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.core.utils.Version;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

public abstract class VersionResolver {

    private static final Logger LOGGER = LogManager.getLogger("VersionResolver");
    private static final VersionResolver INSTANCE = PlatformLoader.load(VersionResolver.class);

    private static final HashMap<String, Optional<Version>> CACHES = new HashMap<>();

    /**
     * Get the target mod version.
     */
    public static Optional<Version> getVersion(String modId) {
        return CACHES.computeIfAbsent(modId, it -> {
            var version = INSTANCE.search(it);
            LOGGER.info("Resolve {} version: {}", it, version);
            if (version != null) {
                return Optional.of(Version.parse(version));
            }
            return Optional.empty();
        });
    }

    @Nullable
    protected abstract String search(String modId);
}
