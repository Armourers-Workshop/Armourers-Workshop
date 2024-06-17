package moe.plushie.armourers_workshop.core.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

public class BufferedTexture extends AbstractTexture {

    private final ITextureProvider provider;

    public BufferedTexture(ITextureProvider provider) {
        this.provider = provider;
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        NativeImage pixels = NativeImage.read(provider.getBuffer());
        TextureUtil.prepareImage(getId(), pixels.getWidth(), pixels.getHeight());
        pixels.upload(0, 0, 0, false);
        pixels.close();
    }
}

