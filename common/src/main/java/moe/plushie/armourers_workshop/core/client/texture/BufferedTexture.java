package moe.plushie.armourers_workshop.core.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BufferedTexture extends AbstractTexture {

    private final ByteBuffer bytes;

    public BufferedTexture(ITextureProvider provider) {
        this.bytes = provider.getBuffer().asReadOnlyBuffer();
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        try (var pixels = NativeImage.read(bytes)) {
            TextureUtil.prepareImage(getId(), pixels.getWidth(), pixels.getHeight());
            pixels.upload(0, 0, 0, true);
        }
    }

    @Override
    public void close() {
        releaseId();
    }
}

