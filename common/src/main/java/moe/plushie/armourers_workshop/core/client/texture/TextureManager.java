package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.texture.TextureAnimation;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class TextureManager {

    private static final AtomicInteger ID = new AtomicInteger(0);
    private static final TextureManager INSTANCE = new TextureManager();

    private final ConcurrentHashMap<ITextureProvider, Entry> textures = new ConcurrentHashMap<>();

    public static TextureManager getInstance() {
        return INSTANCE;
    }

    public void start() {
        ID.set(0);
    }

    public void stop() {
        textures.values().forEach(Entry::close);
        textures.clear();
    }

    public void open(RenderType renderType) {
        var entry = Entry.of(renderType);
        if (entry != null) {
            entry.retain();
        }
    }

    public void close(RenderType renderType) {
        var entry = Entry.of(renderType);
        if (entry != null) {
            entry.release();
        }
    }

    public RenderType register(ITextureProvider provider) {
        return textures.computeIfAbsent(provider, Entry::new).getRenderType();
    }

    public static class Entry {

        private final IResourceLocation location;

        private final RenderType renderType;
        private final BufferedTexture texture;
        private final TextureAnimationController animationController;

        private final AtomicInteger counter = new AtomicInteger(0);

        private boolean isUpload = false;

        public Entry(ITextureProvider provider) {
            this.location = resolveResourceLocation(provider);
            this.renderType = resolveRenderType(provider, location);
            this.texture = new BufferedTexture(provider);
            this.animationController = new TextureAnimationController((TextureAnimation) provider.getAnimation());
            this.resolve();
        }

        @Nullable
        public static TextureManager.Entry of(RenderType renderType) {
            return IAssociatedObjectProvider.get(renderType);
        }

        protected void retain() {
            counter.getAndIncrement();
            if (!isUpload) {
                open();
            }
        }

        protected void release() {
            if (counter.get() > 0 && counter.decrementAndGet() == 0) {
                close();
            }
        }

        protected void open() {
            if (isUpload) {
                return;
            }
            ModLog.debug("upload texture {}", location);
            Minecraft.getInstance().getTextureManager().register(location.toLocation(), texture);
            isUpload = true;
        }

        protected void close() {
            counter.set(0);
            if (!isUpload) {
                return;
            }
            isUpload = false;
            ModLog.debug("close texture {}", location);
            Minecraft.getInstance().getTextureManager().release(location.toLocation());
        }

        public IResourceLocation getLocation() {
            return location;
        }

        public RenderType getRenderType() {
            return renderType;
        }

        public TextureAnimationController getAnimationController() {
            return animationController;
        }

        @Override
        public String toString() {
            return location.toString();
        }

        private void resolve() {
            if (renderType instanceof IAssociatedObjectProvider provider) {
                Entry entry = provider.getAssociatedObject();
                if (entry != null) {
                    entry.close();
                }
                provider.setAssociatedObject(this);
            }
        }

        private IResourceLocation resolveResourceLocation(ITextureProvider provider) {
            var path = "textures/dynamic/" + ID.getAndIncrement();
            var properties = provider.getProperties();
            if (properties.isEmissive()) {
                path += "_s"; // light
            }
            return ModConstants.key(path);
        }

        private RenderType resolveRenderType(ITextureProvider provider, IResourceLocation location) {
            var properties = provider.getProperties();
            if (properties.isEmissive()) {
                return SkinRenderType.customLightingFace(location);
            }
            return SkinRenderType.customSolidFace(location);
        }
    }
}
