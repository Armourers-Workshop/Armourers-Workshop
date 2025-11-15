package moe.plushie.armourers_workshop.core.client.texture;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.other.SmartResourceManager;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureProperties;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SmartTexture extends ReferenceCounted {

    private final OpenResourceLocation location;

    private final SkinTextureProperties properties;
    private final TextureAnimationController animationController;

    private final Map<OpenResourceLocation, ByteBuf> textureBuffers;

    private final Map<SkinGeometryType, IRenderType> bindingRenderTypes = new LinkedHashMap<>();

    public SmartTexture(SkinTextureData provider) {
        this.location = ModConstants.key("textures/dynamic/" + OpenRandomSource.nextInt(SmartTexture.class) + ".png");
        this.properties = provider.properties();
        this.textureBuffers = resolveTextureBuffers(location, provider);
        this.animationController = new TextureAnimationController(provider.animation());
    }

    @Nullable
    public static SmartTexture of(IRenderType renderType) {
        return DataContainer.getOrDefault(renderType, null);
    }

    @Override
    protected void init() {
        RenderSystem.safeCall(() -> {
            textureBuffers.forEach(SmartResourceManager.getInstance()::register);
            SmartTextureManager.getInstance().uploadTexture(this);
        });
    }

    @Override
    protected void dispose() {
        RenderSystem.safeCall(() -> {
            SmartTextureManager.getInstance().releaseTexture(this);
            textureBuffers.keySet().forEach(SmartResourceManager.getInstance()::unregister);
        });
    }

    public IRenderType getRenderType(SkinGeometryType type) {
        return bindingRenderTypes.computeIfAbsent(type, it -> {
            var renderType = SkinRenderType.geometryFace(it, location, properties.isTranslucent(), properties.isEmissive());
            DataContainer.set(renderType, this);
            return renderType;
        });
    }

    public OpenResourceLocation location() {
        return location;
    }

    public TextureAnimationController animationController() {
        return animationController;
    }

    @Override
    public String toString() {
        return location.toString();
    }

    protected void unbind() {
        bindingRenderTypes.forEach((key, value) -> DataContainer.set(value, null));
        // when unbind the object, we must ensure that all resources release.
        while (refCnt() > 0) {
            release();
        }
    }

    private Map<OpenResourceLocation, ByteBuf> resolveTextureBuffers(OpenResourceLocation location, SkinTextureData provider) {
        var path = FileUtils.removeExtension(location.path());
        var builder = new TextureBufferBuilder(provider.properties());
        builder.addData(location, provider);
        for (var variant : provider.variants()) {
            if (variant.properties().isNormal()) {
                builder.addData(location.withPath(path + "_n.png"), variant);
            }
            if (variant.properties().isSpecular()) {
                builder.addData(location.withPath(path + "_s.png"), variant);
            }
        }
        return builder.build();
    }

    private static class TextureBufferBuilder {

        private final Map<OpenResourceLocation, ByteBuf> buffers = new LinkedHashMap<OpenResourceLocation, ByteBuf>();

        private final SkinTextureProperties parentProperties;

        private TextureBufferBuilder(SkinTextureProperties parentProperties) {
            this.parentProperties = parentProperties;
        }

        public void addData(OpenResourceLocation location, SkinTextureData provider) {
            buffers.put(location, provider.buffer());
            addMeta(location, provider.properties());
        }

        private void addMeta(OpenResourceLocation location, SkinTextureProperties properties) {
            var isBlurFilter = properties.isBlurFilter() || parentProperties.isBlurFilter();
            var isClampToEdge = properties.isClampToEdge() || parentProperties.isClampToEdge();
            if (!isBlurFilter && !isClampToEdge) {
                return; // not needs.
            }
            // https://minecraft.wiki/w/Resource_pack#Properties
            var blur = String.valueOf(isBlurFilter);
            var clamp = String.valueOf(isClampToEdge);
            var meta = String.format("{\"texture\":{\"blur\":%s,\"clamp\":%s}}", blur, clamp);
            buffers.put(location.withPath(location.path() + ".mcmeta"), Unpooled.wrappedBuffer(meta.getBytes()));
        }

        public Map<OpenResourceLocation, ByteBuf> build() {
            return buffers;
        }
    }
}
