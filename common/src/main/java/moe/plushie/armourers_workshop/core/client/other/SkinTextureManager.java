package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import moe.plushie.armourers_workshop.api.common.ITextureProperties;
import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ext.OpenResourceLocation;
import moe.plushie.armourers_workshop.utils.texture.TextureAnimation;
import moe.plushie.armourers_workshop.utils.texture.TextureAnimationController;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class SkinTextureManager {

    private static final SkinTextureManager INSTANCE = new SkinTextureManager();

    private final AtomicInteger counter = new AtomicInteger(0);
    private final TextureManager textureManager = Minecraft.getInstance().getTextureManager();
    private final ConcurrentHashMap<ITextureProvider, IResourceLocation> textures = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<ITextureProvider, RenderType> renderTypes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ITextureProvider, Collection<RenderType>> renderTypeVariants = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<RenderType, TextureAnimationController> textureAnimationControllers = new ConcurrentHashMap<>();


    public static SkinTextureManager getInstance() {
        return INSTANCE;
    }


    public RenderType prepareTexture(ITextureProvider provider) {
        return renderTypes.computeIfAbsent(provider, it -> registerAnimation(SkinRenderType.solidFace(registerTexture(it)), it));
    }

    public Collection<RenderType> prepareVariantTextures(ITextureProvider provider) {
        return renderTypeVariants.computeIfAbsent(provider, it -> {
            Collection<ITextureProvider> varinats = it.getVariants();
            if (varinats != null) {
                return ObjectUtils.map(varinats, varinat -> registerAnimation(SkinRenderType.lightingFace(registerTexture(varinat, it)), varinat));
            }
            return null;
        });
    }

    public TextureAnimationController getTextureAnimationController(RenderType renderType) {
        // by default render type, we will use special animation.
        if (isDefault(renderType)) {
            return TextureAnimationController.DEFAULT;
        }
        return textureAnimationControllers.getOrDefault(renderType, TextureAnimationController.NONE);
    }

    public void start() {
    }

    public void stop() {
        textures.values().forEach(it -> textureManager.release(it.toLocation()));
        textures.clear();
        renderTypes.clear();
        renderTypeVariants.clear();
        textureAnimationControllers.clear();
        counter.set(0);
    }


    private IResourceLocation registerTexture(ITextureProvider provider) {
        return textures.computeIfAbsent(provider, k -> {
            IResourceLocation rl = ModConstants.key("textures/dymanic/" + counter.getAndIncrement());
            return registerTexture(rl, provider);
        });
    }

    private IResourceLocation registerTexture(ITextureProvider variant, ITextureProvider parent) {
        return textures.computeIfAbsent(variant, k -> {
            IResourceLocation rl = registerTexture(parent);
            rl = OpenResourceLocation.create(rl.getNamespace(), rl.getPath() + "_" + getTextureType(variant));
            return registerTexture(rl, variant);
        });
    }

    private IResourceLocation registerTexture(IResourceLocation rl, ITextureProvider provider) {
        ModLog.debug("Registering Dynamic Texture '{}', {}", rl, provider);
        RenderSystem.recordRenderCall(() -> textureManager.register(rl.toLocation(), new CustomTexture(provider)));
        return rl;
    }

    private RenderType registerAnimation(RenderType renderType, ITextureProvider textureProvider) {
        TextureAnimationController animation = new TextureAnimationController((TextureAnimation) textureProvider.getAnimation());
        textureAnimationControllers.put(renderType, animation);
        return renderType;
    }

    private String getTextureType(ITextureProvider provider) {
        ITextureProperties properties = provider.getProperties();
        if (properties.isEmissive()) {
            return "s"; // light
        }
        return String.format("%04x", properties.hashCode());
    }

    private boolean isDefault(RenderType renderType) {
        return renderType == SkinRenderType.FACE_SOLID || renderType == SkinRenderType.FACE_LIGHTING || renderType == SkinRenderType.FACE_TRANSLUCENT || renderType == SkinRenderType.FACE_LIGHTING_TRANSLUCENT;
    }

    public static class CustomTexture extends AbstractTexture {

        private final ITextureProvider provider;

        CustomTexture(ITextureProvider provider) {
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
}
