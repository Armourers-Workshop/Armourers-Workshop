package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelCube;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelTexture;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelUV;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Size2f;
import moe.plushie.armourers_workshop.utils.texture.TextureAnimation;
import moe.plushie.armourers_workshop.utils.texture.TextureBox;
import moe.plushie.armourers_workshop.utils.texture.TextureData;
import moe.plushie.armourers_workshop.utils.texture.TextureProperties;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BlockBenchModelTexture extends BedrockModelTexture {

    private final Size2f resolution;
    private final List<BlockBenchTexture> inputs;
    private final HashMap<Integer, TextureData> textureDatas = new HashMap<>();
    private final HashMap<String, TextureData> allLoadedTextures = new HashMap<>();

    protected TextureData defaultTextureData;

    public BlockBenchModelTexture(Size2f resolution, List<BlockBenchTexture> textureInputs) {
        this.resolution = resolution;
        this.inputs = textureInputs;
    }

    public void load(HashSet<Integer> usedTextureIds) throws IOException {
        if (defaultTextureData != null) {
            return;
        }
        for (int textureId : usedTextureIds) {
            // ignore invalid textures.
            if (textureId < 0 || textureId >= inputs.size()) {
                continue;
            }
            BlockBenchTexture texture = inputs.get(textureId);
            TextureData data = loadTextureData(texture);
            textureDatas.put(textureId, data);
            if (defaultTextureData == null) {
                defaultTextureData = data;
            }
            BlockBenchTexture additionalTexture = getAdditionalTexture(texture);
            if (additionalTexture != null) {
                TextureData variant = loadTextureData(additionalTexture);
                variant.getProperties().setEmissive(true);
                data.setVariants(Collections.singleton(variant));
            }
        }
        textureData = defaultTextureData;
        if (textureData == null) {
            throw new IOException("error.bb.loadModel.noTexture");
        }
    }

    @Override
    public TextureBox read(BedrockModelCube cube) {
        textureData = getTextureData(cube.getUV());
        return super.read(cube);
    }

    private BlockBenchTexture getAdditionalTexture(BlockBenchTexture texture) {
        String name = texture.getName();
        for (BlockBenchTexture input : inputs) {
            String target = input.getName();
            if (name.length() < target.length() && name.equals(target.replaceAll("(?i)_s", ""))) {
                return input;
            }
        }
        return null;
    }

    private TextureData getTextureData(BedrockModelUV uv) {
        BlockBenchModelUV uv1 = ObjectUtils.safeCast(uv, BlockBenchModelUV.class);
        if (uv1 != null) {
            return textureDatas.getOrDefault(uv1.getTextureId(), defaultTextureData);
        }
        return defaultTextureData;
    }

    private TextureData loadTextureData(BlockBenchTexture texture) throws IOException {
        TextureData textureData = allLoadedTextures.get(texture.getUUID());
        if (textureData != null) {
            return textureData;
        }
        String str = texture.getSource();
        String[] parts = str.split(";base64,");
        if (parts.length != 2) {
            throw new IOException("error.bb.loadModel.textureNotSupported");
        }
        TextureProperties properties = texture.getProperties();
        byte[] imageBytes = Base64.getDecoder().decode(parts[1]);
        Size2f size = resolveTextureSize(imageBytes, texture, properties);
        textureData = new TextureData(texture.getName(), size.getWidth(), size.getHeight(), properties);
        textureData.load(Unpooled.wrappedBuffer(imageBytes));
        allLoadedTextures.put(texture.getUUID(), textureData);
        return textureData;
    }

    private Size2f resolveTextureSize(byte[] imageBytes, BlockBenchTexture texture, TextureProperties properties) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        if (imageWidth == 0 || imageHeight == 0) {
            throw new IOException("inaild image");
        }
        float width = resolution.getWidth();
        float height = resolution.getHeight();
        int frameCount = imageHeight / imageWidth;
        if (frameCount > 1 && imageHeight % imageWidth == 0) {
            TextureAnimation.Mode mode = texture.getAnimationMode();
//            properties.setAnimation(new TextureAnimation(imageWidth, imageWidth, frameCount, mode));
            height = imageHeight * (width / imageWidth);
        }
        return new Size2f(width, height);
    }
}
