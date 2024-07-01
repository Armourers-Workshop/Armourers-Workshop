package moe.plushie.armourers_workshop.core.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

public class BufferedTexture extends AbstractTexture {

    private NativeImage pixels;

    private final ITextureProvider provider;

    public BufferedTexture(ITextureProvider provider) {
        this.provider = provider;
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        if (pixels == null) {
            pixels = NativeImage.read(provider.getBuffer());
            TextureUtil.prepareImage(getId(), pixels.getWidth(), pixels.getHeight());
            pixels.upload(0, 0, 0, false);
        }
    }

    @Override
    public void close() {
        if (pixels != null) {
            pixels.close();
            releaseId();
            pixels = null;
        }
    }
}

