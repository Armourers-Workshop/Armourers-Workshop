package moe.plushie.armourers_workshop.compatibility.core.data;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.texture.SmartResourceManager;
import moe.plushie.armourers_workshop.utils.ext.OpenResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

@Available("[1.21, )")
public class AbstractPackResources implements PackResources {

    private final PackLocationInfo location;
    private final SmartResourceManager resourceManager;

    public AbstractPackResources(SmartResourceManager resourceManager, PackType packType) {
        this.location = new PackLocationInfo(resourceManager.getId(), Component.empty(), PackSource.DEFAULT, Optional.empty());
        this.resourceManager = resourceManager;
    }

    public static boolean isModResources(PackResources resources) {
        return !resources.packId().startsWith("file/");
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return resourceManager.getNamespaces(packType);
    }

    @Nullable
    @Override
    public final IoSupplier<InputStream> getRootResource(String... strings) {
        return null;
    }

    @Nullable
    @Override
    public final IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        var supplier = resourceManager.getResource(packType, OpenResourceLocation.create(location));
        if (supplier != null) {
            return supplier::get;
        }
        return null;
    }

    @Nullable
    @Override
    public final <T> T getMetadataSection(MetadataSectionSerializer<T> metadataSectionSerializer) throws IOException {
        return null;
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput output) {
        // nope.
    }

    @Override
    public final PackLocationInfo location() {
        return location;
    }

    @Override
    public void close() {
        // reload or quit.
    }
}
