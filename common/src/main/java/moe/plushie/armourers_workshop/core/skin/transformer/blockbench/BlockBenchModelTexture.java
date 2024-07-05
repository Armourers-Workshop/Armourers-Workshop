package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelCube;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelTexture;
import moe.plushie.armourers_workshop.core.texture.TextureAnimation;
import moe.plushie.armourers_workshop.core.texture.TextureData;
import moe.plushie.armourers_workshop.core.texture.TextureProperties;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.Size2f;
import net.minecraft.core.Direction;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public class BlockBenchModelTexture extends BedrockModelTexture {

    private static final String PATTERN = "^(.+)_([nes]+)(\\.\\w+)?$";

    private final Size2f resolution;
    private final List<BlockBenchTexture> inputs;
    private final HashMap<Integer, TextureData> allTexture = new HashMap<>();
    private final HashMap<String, TextureData> loadedTextures = new HashMap<>();

    protected TextureData defaultTextureData;

    public BlockBenchModelTexture(Size2f resolution, List<BlockBenchTexture> textureInputs) {
        this.resolution = resolution;
        this.inputs = textureInputs;
    }

    public void load(HashSet<Integer> usedTextureIds) throws IOException {
        if (defaultTextureData != null) {
            return;
        }
        for (var textureId : usedTextureIds) {
            // ignore invalid textures.
            if (textureId < 0 || textureId >= inputs.size()) {
                continue;
            }
            var texture = inputs.get(textureId);
            var data = loadTextureData(texture);
            allTexture.put(textureId, data);
            if (defaultTextureData == null) {
                defaultTextureData = data;
            }
        }
        textureData = defaultTextureData;
        if (textureData == null) {
            throw new IOException("error.bb.loadModel.noTexture");
        }
    }

    @Override
    protected TextureData getTextureData(BedrockModelCube cube) {
        if (cube.getUV() instanceof BlockBenchModelUV uv) {
            return allTexture.get(uv.getDefaultTextureId());
        }
        return null;
    }

    @Override
    protected TextureData getTextureData(BedrockModelCube cube, Direction dir) {
        if (cube.getUV() instanceof BlockBenchModelUV uv) {
            return allTexture.get(uv.getTextureId(dir));
        }
        return null;
    }

    private TextureData loadTextureData(BlockBenchTexture texture) throws IOException {
        var data = resolveTextureData(texture);
        var variants = new ArrayList<ITextureProvider>();
        var parentName = texture.getName().replaceAll(PATTERN, "$1$3");
        var parentAttributes = getTextureAttributes(texture.getName());
        // some models only support single texture, so load additional textures by special file names.
        for (var childTexture : inputs) {
            var childName = childTexture.getName().replaceAll(PATTERN, "$1$3");
            if (!childName.equals(parentName) || childTexture == texture) {
                continue;
            }
            var childAttributes = getTextureAttributes(childTexture.getName());
            if (!childAttributes.containsAll(parentAttributes)) {
                continue;
            }
            var childData = resolveTextureData(childTexture);
            if (data.getProperties().isEmissive()) {
                // when the parent texture is emissive texture, the child texture must is emissive texture.
                childData.getProperties().setEmissive(true);
            }
            variants.add(childData);
        }
        data.setVariants(variants);
        Resolution.apply(data);
        return data;
    }

    private TextureData resolveTextureData(BlockBenchTexture texture) throws IOException {
        var textureData = loadedTextures.get(texture.getUUID());
        if (textureData != null) {
            return textureData;
        }
        var str = texture.getSource();
        var parts = str.split(";base64,");
        if (parts.length != 2) {
            throw new IOException("error.bb.loadModel.textureNotSupported");
        }
        var imageBytes = Base64.getDecoder().decode(parts[1]);
        var imageFrame = resolveTextureFrame(texture, imageBytes);
        var size = resolveTextureSize(texture, imageFrame);
        var animation = resolveTextureAnimation(texture, imageFrame);
        var properties = resolveTextureProperties(texture);
        textureData = new TextureData(texture.getName(), size.getWidth(), size.getHeight(), animation, properties);
        textureData.load(Unpooled.wrappedBuffer(imageBytes));
        loadedTextures.put(texture.getUUID(), textureData);
        return textureData;
    }

    private int resolveTextureFrame(BlockBenchTexture texture, byte[] imageBytes) throws IOException {
        // in new version block bench provides image size.
        var imageSize = texture.getImageSize();
        if (imageSize == null) {
            var image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            imageSize = new Size2f(image.getWidth(), image.getHeight());
        }
        var width = MathUtils.floor(imageSize.getWidth());
        var height = MathUtils.floor(imageSize.getHeight());
        var frame = height / width;
        if (frame * width == height) {
            return frame;
        }
        return 0;
    }

    private Size2f resolveTextureSize(BlockBenchTexture texture, int frameCount) {
        var width = resolution.getWidth();
        var height = resolution.getHeight();
        // in new version block bench provides texture size.
        if (texture.getTextureSize() != null) {
            width = texture.getTextureSize().getWidth();
            height = texture.getTextureSize().getHeight();
        }
        if (frameCount > 1) {
            height = width * frameCount;
        }
        return new Size2f(width, height);
    }

    private TextureAnimation resolveTextureAnimation(BlockBenchTexture texture, int frameCount) {
        if (frameCount > 1) {
            var time = texture.getFrameTime() * 50;
            var interpolate = texture.getFrameInterpolate();
            var mode = texture.getFrameMode();
            return new TextureAnimation(time, frameCount, mode, interpolate);
        }
        return TextureAnimation.EMPTY;
    }

    private TextureProperties resolveTextureProperties(BlockBenchTexture texture) {
        var properties = texture.getProperties();
        for (String attrib : getTextureAttributes(texture.getName())) {
            switch (attrib) {
                case "n" -> properties.setNormal(true);
                case "e" -> properties.setEmissive(true);
                case "s" -> properties.setSpecular(true);
            }
        }
        return properties;
    }

    private Collection<String> getTextureAttributes(String name) {
        var attrib = name.replaceAll(PATTERN, "$2");
        if (attrib.equals(name)) {
            return Collections.emptyList();
        }
        var results = new HashSet<String>();
        for (byte ch : attrib.getBytes(StandardCharsets.UTF_8)) {
            results.add(String.valueOf((char) ch));
        }
        return results;
    }

    public static class Resolution {

        public static void apply(TextureData data) {
            var base = by(data);
            var variants = data.getVariants();
            if (variants.isEmpty()) {
                data.setVariants(Collections.emptyList());
                return;
            }
            var secondaryTextures = new LinkedHashMap<Integer, ITextureProvider>();
            var additionalTextures = new LinkedHashMap<Integer, List<ITextureProvider>>();
            secondaryTextures.put(base & 0xf0, data);
            for (var variant : variants) {
                var key = by(variant);
                if ((key & 0x0f) == 0) {
                    // is secondary texture.
                    secondaryTextures.putIfAbsent(key & 0xf0, variant);
                } else {
                    // is additional texture.
                    additionalTextures.computeIfAbsent(key & 0xf0, k -> new ArrayList<>()).add(variant);
                }
            }
            secondaryTextures.forEach((key, provider) -> {
                if (provider instanceof TextureData data1) {
                    var used = by(data1) & 0x0f;
                    var values = additionalTextures.getOrDefault(key, new ArrayList<>());
                    var iterator = values.iterator();
                    while (iterator.hasNext()) {
                        var ck = by(iterator.next()) & 0x0f;
                        if ((used & ck) == ck) {
                            iterator.remove();
                        }
                        used |= ck;
                    }
                    data1.setVariants(values);
                }
            });
            var newVariants = new ArrayList<>(secondaryTextures.values());
            newVariants.remove(data);
            newVariants.addAll(data.getVariants());
            data.setVariants(newVariants);
        }

        public static int by(ITextureProvider data) {
            int key = 0;
            var properties = data.getProperties();
            if (properties.isEmissive()) {
                key |= 0x10;
            }
            if (properties.isParticle()) {
                key |= 0x20;
            }
            if (properties.isNormal()) {
                key |= 0x01;
            }
            if (properties.isSpecular()) {
                key |= 0x02;
            }
            return key;
        }
    }
}
