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

    public RenderType register(ITextureProvider provider) {
        return textures.computeIfAbsent(provider, Entry::new).getRenderType();
    }

    public static class Entry {

        private final IResourceLocation location;

        private final RenderType renderType;
        private final SmartBufferedTexture bufferedTexture;
        private final TextureAnimationController animationController;

        public Entry(ITextureProvider provider) {
            this.location = resolveResourceLocation(provider);
            this.renderType = resolveRenderType(location, provider);
            this.bufferedTexture = new SmartBufferedTexture(location, provider.getBuffer());
            this.animationController = new TextureAnimationController((TextureAnimation) provider.getAnimation());
            this.open();
        }

        @Nullable
        public static TextureManager.Entry of(RenderType renderType) {
            return IAssociatedObjectProvider.get(renderType);
        }

        protected void open() {
            // close old texture if needs.
            if (renderType instanceof IAssociatedObjectProvider provider) {
                Entry entry = provider.getAssociatedObject();
                if (entry != null) {
                    entry.close();
                }
                provider.setAssociatedObject(this);
            }
            ModLog.debug("Registering Texture '{}'", location);
            Minecraft.getInstance().getTextureManager().register(location.toLocation(), bufferedTexture);
        }

        protected void close() {
            ModLog.debug("Unregistering Texture '{}'", location);
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

        private IResourceLocation resolveResourceLocation(ITextureProvider provider) {
            var path = "textures/dynamic/" + ID.getAndIncrement();
            var properties = provider.getProperties();
            if (properties.isEmissive()) {
                path += "_s"; // light
            }
            return ModConstants.key(path);
        }

        private RenderType resolveRenderType(IResourceLocation location, ITextureProvider provider) {
            var properties = provider.getProperties();
            if (properties.isEmissive()) {
                return SkinRenderType.customLightingFace(location);
            }
            return SkinRenderType.customSolidFace(location);
        }
    }
}
