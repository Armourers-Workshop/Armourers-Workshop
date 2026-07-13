package moe.plushie.armourers_workshop.core.client.texture;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.core.client.other.SmartResourceManager;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureProperties;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class SmartTexture extends ReferenceCounted {

    private static final DataContainer.Key<SmartTexture> KEY = DataContainer.key("SmartTexture");

    private final int id = OpenRandomSource.nextInt(SmartTexture.class);

    private final OpenResourceKey location;

    private final SkinTextureProperties properties;
    private final TextureAnimationController animationController;

    private final Map<OpenResourceKey, ByteBuf> buffers;

    private final Set<IRenderType> binding = new HashSet<>();

    protected SmartTexture(SkinTextureData textureData) {
        this.location = ModConstants.key("textures/dynamic/" + id + "." + textureData.extension());
        this.properties = textureData.properties();
        this.buffers = resolveTextureBuffers(location, textureData);
        this.animationController = new TextureAnimationController(textureData.animation());
    }

    @Nullable
    public static SmartTexture of(IRenderType renderType) {
        return DataContainer.get(renderType, KEY);
    }

    public IRenderType create(Function<SmartTexture, IRenderType> factory) {
        var renderType = factory.apply(this);
        if (binding.add(renderType)) {
            DataContainer.set(renderType, KEY, this);
        }
        return renderType;
    }

    @Override
    protected void init() {
        RenderSystem.safeCall(() -> {
            buffers.forEach(SmartResourceManager.getInstance()::register);
            SmartTextureManager.getInstance().uploadTexture(this);
        });
    }

    @Override
    protected void dispose() {
        RenderSystem.safeCall(() -> {
            SmartTextureManager.getInstance().releaseTexture(this);
            buffers.keySet().forEach(SmartResourceManager.getInstance()::unregister);
        });
    }

    protected void close() {
        binding.forEach(value -> DataContainer.set(value, KEY, null));
        RenderSystem.safeCall(() -> {
            // when unbind the object, we must ensure that all resources release.
            while (refCnt() > 0) {
                release();
            }
            // release all byte buffers.
            buffers.values().forEach(ByteBuf::release);
            buffers.clear();
        });
    }

    public int id() {
        return id;
    }

    public OpenResourceKey location() {
        return location;
    }

    public SkinTextureProperties properties() {
        return properties;
    }

    public TextureAnimationController animationController() {
        return animationController;
    }

    public boolean isTranslucent() {
        return properties.isTranslucent();
    }

    public boolean isEmissive() {
        return properties.isEmissive();
    }

    @Override
    public String toString() {
        return location.toString();
    }

    private Map<OpenResourceKey, ByteBuf> resolveTextureBuffers(OpenResourceKey key, SkinTextureData textureData) {
        var path = FileUtils.removeExtension(key.path());
        var builder = new TextureBufferBuilder(textureData.properties());
        builder.addData(key, textureData);
        for (var variant : textureData.variants()) {
            if (variant.properties().isNormal()) {
                builder.addData(key.withPath(path + "_n.png"), variant);
            }
            if (variant.properties().isSpecular()) {
                builder.addData(key.withPath(path + "_s.png"), variant);
            }
        }
        return builder.build();
    }

    private static class TextureBufferBuilder {

        private final Map<OpenResourceKey, ByteBuf> buffers = new LinkedHashMap<>();

        private final SkinTextureProperties parentProperties;

        private TextureBufferBuilder(SkinTextureProperties parentProperties) {
            this.parentProperties = parentProperties;
        }

        public void addData(OpenResourceKey key, SkinTextureData textureData) {
            buffers.put(key, Unpooled.wrappedBuffer(textureData.bytes()));
            addMeta(key, textureData.properties());
        }

        private void addMeta(OpenResourceKey key, SkinTextureProperties properties) {
            var isBlurFilter = properties.isBlurFilter() || parentProperties.isBlurFilter();
            var isClampToEdge = properties.isClampToEdge() || parentProperties.isClampToEdge();
            if (!isBlurFilter && !isClampToEdge) {
                return; // not needs.
            }
            // https://minecraft.wiki/w/Resource_pack#Properties
            var blur = String.valueOf(isBlurFilter);
            var clamp = String.valueOf(isClampToEdge);
            var meta = String.format("{\"texture\":{\"blur\":%s,\"clamp\":%s}}", blur, clamp);
            buffers.put(key.withPath(key.path() + ".mcmeta"), Unpooled.copiedBuffer(meta, StandardCharsets.UTF_8));
        }

        public Map<OpenResourceKey, ByteBuf> build() {
            return buffers;
        }
    }
}
