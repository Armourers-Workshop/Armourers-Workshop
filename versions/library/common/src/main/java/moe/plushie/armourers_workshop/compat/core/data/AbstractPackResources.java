package moe.plushie.armourers_workshop.compat.core.data;

import moe.plushie.armourers_workshop.core.client.other.SmartResourceManager;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.server.packs.PackType;

import java.io.InputStream;
import java.util.Set;
import java.util.function.Supplier;

public class AbstractPackResources extends AbstractPackResourcesImpl {

    private final String id;
    private final SmartResourceManager resourceManager;

    public AbstractPackResources(SmartResourceManager resourceManager, PackType packType) {
        this.id = resourceManager.id();
        this.resourceManager = resourceManager;
    }

    @Override
    public void close() {
        // reload or quit.
    }

    @Override
    public Supplier<InputStream> getResource(PackType packType, OpenResourceKey key) {
        return resourceManager.getResource(packType, key);
    }

    @Override
    public final Set<String> getNamespaces(PackType packType) {
        return resourceManager.getNamespaces(packType);
    }

    @Override
    public String getName() {
        return id;
    }
}
