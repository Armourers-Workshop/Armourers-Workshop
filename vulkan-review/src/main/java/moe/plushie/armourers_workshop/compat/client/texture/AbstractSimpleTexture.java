package moe.plushie.armourers_workshop.compat.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import moe.plushie.armourers_workshop.core.client.other.SmartResourceManager;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class AbstractSimpleTexture {
    public static SimpleTexture create(OpenResourceKey key) {
        return new SimpleTexture(key.get()) {
            @Override public TextureContents loadContents(ResourceManager manager) throws IOException {
                var memory = SmartResourceManager.getInstance().getResource(PackType.CLIENT_RESOURCES, key);
                if (memory == null) return super.loadContents(manager);
                try (var stream = memory.get()) {
                    var metadataStream = SmartResourceManager.getInstance().getResource(PackType.CLIENT_RESOURCES, key.withPath(key.path() + ".mcmeta"));
                    net.minecraft.client.resources.metadata.texture.TextureMetadataSection metadata = null;
                    if (metadataStream != null) {
                        try (var reader = new java.io.InputStreamReader(metadataStream.get(), java.nio.charset.StandardCharsets.UTF_8)) {
                            var root = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
                            if (root.has("texture")) {
                                metadata = net.minecraft.client.resources.metadata.texture.TextureMetadataSection.CODEC
                                    .parse(com.mojang.serialization.JsonOps.INSTANCE, root.get("texture"))
                                    .getOrThrow(message -> new IOException("Invalid dynamic texture metadata: " + message));
                            }
                        }
                    }
                    return new TextureContents(NativeImage.read(stream), metadata);
                }
            }
        };
    }
}
