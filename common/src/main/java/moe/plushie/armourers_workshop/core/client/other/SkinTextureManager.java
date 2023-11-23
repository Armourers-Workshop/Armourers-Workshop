package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import moe.plushie.armourers_workshop.api.common.ITextureProperties;
import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.texture.TextureAnimationController;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
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
    private final ConcurrentHashMap<ITextureProvider, ResourceLocation> textures = new ConcurrentHashMap<>();

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
        if (renderType == SkinRenderType.FACE_SOLID || renderType == SkinRenderType.FACE_LIGHTING || renderType == SkinRenderType.FACE_TRANSLUCENT || renderType == SkinRenderType.FACE_LIGHTING_TRANSLUCENT) {
            return TextureAnimationController.DEFAULT;
        }
        return textureAnimationControllers.getOrDefault(renderType, TextureAnimationController.NONE);
    }

    public void start() {
    }

    public void stop() {
        textures.values().forEach(textureManager::release);
        textures.clear();
        renderTypes.clear();
        renderTypeVariants.clear();
        counter.set(0);
    }


    private ResourceLocation registerTexture(ITextureProvider provider) {
        return textures.computeIfAbsent(provider, k -> {
            ResourceLocation rl = ModConstants.key("textures/dymanic/" + counter.getAndIncrement());
            return registerTexture(rl, provider);
        });
    }

    private ResourceLocation registerTexture(ITextureProvider variant, ITextureProvider parent) {
        return textures.computeIfAbsent(variant, k -> {
            ResourceLocation rl = registerTexture(parent);
            rl = new ResourceLocation(rl.getNamespace(), rl.getPath() + "_" + getTextureType(variant));
            return registerTexture(rl, variant);
        });
    }

    private ResourceLocation registerTexture(ResourceLocation rl, ITextureProvider provider) {
        ModLog.debug("register dynamic texture: {} => {}", rl, provider);
        RenderSystem.recordRenderCall(() -> textureManager.register(rl, new CustomTexture(provider)));
        return rl;
    }

    private RenderType registerAnimation(RenderType renderType, ITextureProvider textureProvider) {
//        TextureAnimation animation = new TextureAnimation(textureProvider);
//        textureAnimationControllers.put(renderType, animation);
        return renderType;
    }

    private String getTextureType(ITextureProvider provider) {
        ITextureProperties properties = provider.getProperties();
        if (properties.isEmissive()) {
            return "s"; // light
        }
        return String.format("%04x", properties.hashCode());
    }

    public static class CustomTexture extends AbstractTexture {

        private final ITextureProvider provider;

        CustomTexture(ITextureProvider provider) {
            this.provider = provider;
        }

        @Override
        public void load(ResourceManager resourceManager) throws IOException {
            ModLog.debug("start load dynamic texture: {}", provider);
            NativeImage pixels = NativeImage.read(provider.getBuffer());
            TextureUtil.prepareImage(getId(), pixels.getWidth(), pixels.getHeight());
            pixels.upload(0, 0, 0, false);
            pixels.close();
            ModLog.debug("did load dynamic texture: {}", provider);
        }
    }
}
