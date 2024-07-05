package moe.plushie.armourers_workshop.core.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.data.cache.CacheQueue;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.TextureUtils;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;

public class SmartBufferedTexture extends AbstractTexture {

    private static final CacheQueue<Object, Holder> CACHING = new CacheQueue<>(Duration.ofSeconds(30), Holder::close);

    private final IResourceLocation location;
    private final NativeImage pixels;

    public SmartBufferedTexture(IResourceLocation location, ByteBuffer buffer) {
        this.location = location;
        this.pixels = TextureUtils.readTextureImage(buffer);
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        // in vanilla, register texture will calls load immediately.
        // but we hope upload before the actually using it to reduce unnecessary memory.
    }

    @Override
    public void close() {
        var holder = CACHING.remove(this);
        if (holder != null) {
            holder.close();
        }
        // don't forget the image.
        pixels.close();
    }

    @Override
    public void bind() {
        // note we call get every time, because we need to update access time for cache.
        var holder = CACHING.get(this);
        if (holder != null) {
            super.bind();
            return;
        }
        // automatic upload and bind when first call.
        holder = new Holder(this);
        holder.upload(pixels);
        CACHING.put(this, holder);
        super.bind();
    }

    protected static class Holder {

        private final SmartBufferedTexture owner;

        protected Holder(SmartBufferedTexture texture) {
            owner = texture;
        }

        protected void upload(NativeImage pixels) {
            TextureUtil.prepareImage(owner.getId(), pixels.getWidth(), pixels.getHeight());
            pixels.upload(0, 0, 0, false);
            ModLog.debug("Open Texture '{}'", owner.location);
        }

        protected void close() {
            ModLog.debug("Close Texture '{}'", owner.location);
            owner.releaseId();
        }
    }
}

