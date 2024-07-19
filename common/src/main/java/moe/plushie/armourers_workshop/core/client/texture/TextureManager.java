package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.data.cache.ReferenceCounted;
import moe.plushie.armourers_workshop.core.texture.TextureAnimation;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.SimpleTexture;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class TextureManager {

    private static final AtomicInteger ID = new AtomicInteger(0);
    private static final TextureManager INSTANCE = new TextureManager();

    private final IdentityHashMap<ITextureProvider, Entry> textures = new IdentityHashMap<>();

    public static TextureManager getInstance() {
        return INSTANCE;
    }

    public synchronized void start() {
        ID.set(0);
    }

    public synchronized void stop() {
        textures.values().forEach(it -> it.bind(null));
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

    public synchronized RenderType register(ITextureProvider provider) {
        var entry = textures.get(provider);
        if (entry == null) {
            entry = new Entry(provider);
            textures.put(provider, entry);
        }
        return entry.getRenderType();
    }

    public static class Entry extends ReferenceCounted {

        private final IResourceLocation location;

        private final RenderType renderType;
        private final Map<IResourceLocation, ByteBuffer> textureBuffers;
        private final TextureAnimationController animationController;

        public Entry(ITextureProvider provider) {
            this.location = resolveResourceLocation(provider);
            this.renderType = resolveRenderType(location, provider);
            this.textureBuffers = resolveTextureBuffers(location, provider);
            this.animationController = new TextureAnimationController((TextureAnimation) provider.getAnimation());
            this.bind(this);
        }

        @Nullable
        public static TextureManager.Entry of(RenderType renderType) {
            return IAssociatedObjectProvider.get(renderType);
        }

        @Override
        protected void init() {
            RenderSystem.recordRenderCall(() -> {
                textureBuffers.forEach(SmartResourceManager.getInstance()::register);
                Minecraft.getInstance().getTextureManager().register(location.toLocation(), new SimpleTexture(location.toLocation()));
            });
        }

        @Override
        protected void dispose() {
            RenderSystem.recordRenderCall(() -> {
                textureBuffers.keySet().forEach(SmartResourceManager.getInstance()::unregister);
                Minecraft.getInstance().getTextureManager().unregister(location.toLocation());
            });
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

        protected void bind(Entry newValue) {
            IAssociatedObjectProvider provider = ObjectUtils.unsafeCast(renderType);
            provider.setAssociatedObject(newValue);
        }

        private IResourceLocation resolveResourceLocation(ITextureProvider provider) {
            var path = "textures/dynamic/" + ID.getAndIncrement();
            return ModConstants.key(path + ".png");
        }

        private RenderType resolveRenderType(IResourceLocation location, ITextureProvider provider) {
            var properties = provider.getProperties();
            if (properties.isEmissive()) {
                return SkinRenderType.customLightingFace(location);
            }
            return SkinRenderType.customSolidFace(location);
        }

        private Map<IResourceLocation, ByteBuffer> resolveTextureBuffers(IResourceLocation location, ITextureProvider provider) {
            var path = SkinFileUtils.removeExtension(location.getPath());
            var results = new LinkedHashMap<IResourceLocation, ByteBuffer>();
            results.put(location, provider.getBuffer());
            for (var variant : provider.getVariants()) {
                if (variant.getProperties().isNormal()) {
                    results.put(ModConstants.key(path + "_n.png"), variant.getBuffer());
                }
                if (variant.getProperties().isSpecular()) {
                    results.put(ModConstants.key(path + "_s.png"), variant.getBuffer());
                }
            }
            return results;
        }
    }
}
