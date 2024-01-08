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
import net.minecraft.core.Direction;

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
        for (int textureId : usedTextureIds) {
            // ignore invalid textures.
            if (textureId < 0 || textureId >= inputs.size()) {
                continue;
            }
            BlockBenchTexture texture = inputs.get(textureId);
            TextureData data = loadTextureData(texture);
            allTexture.put(textureId, data);
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
    protected TextureData getTextureData(BedrockModelCube cube) {
        BlockBenchModelUV uv1 = ObjectUtils.safeCast(cube.getUV(), BlockBenchModelUV.class);
        if (uv1 != null) {
            return allTexture.get(uv1.getDefaultTextureId());
        }
        return null;
    }

    @Override
    protected TextureData getTextureData(BedrockModelCube cube, Direction dir) {
        BlockBenchModelUV uv1 = ObjectUtils.safeCast(cube.getUV(), BlockBenchModelUV.class);
        if (uv1 != null) {
            return allTexture.get(uv1.getTextureId(dir));
        }
        return null;
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

    private TextureData loadTextureData(BlockBenchTexture texture) throws IOException {
        TextureData textureData = loadedTextures.get(texture.getUUID());
        if (textureData != null) {
            return textureData;
        }
        String str = texture.getSource();
        String[] parts = str.split(";base64,");
        if (parts.length != 2) {
            throw new IOException("error.bb.loadModel.textureNotSupported");
        }
        byte[] imageBytes = Base64.getDecoder().decode(parts[1]);
        int imageFrame = resolveTextureFrame(imageBytes);
        Size2f size = resolveTextureSize(imageFrame);
        TextureAnimation animation = resolveTextureAnimation(texture, imageFrame);
        TextureProperties properties = texture.getProperties();
        textureData = new TextureData(texture.getName(), size.getWidth(), size.getHeight(), animation, properties);
        textureData.load(Unpooled.wrappedBuffer(imageBytes));
        loadedTextures.put(texture.getUUID(), textureData);
        return textureData;
    }

    private int resolveTextureFrame(byte[] imageBytes) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        int width = image.getWidth();
        int height = image.getHeight();
        int frame = height / width;
        if (frame * width == height) {
            return frame;
        }
        return 0;
    }

    private Size2f resolveTextureSize(int frameCount) {
        float width = resolution.getWidth();
        float height = resolution.getHeight();
        if (frameCount > 1) {
            height = width * frameCount;
        }
        return new Size2f(width, height);
    }

    private TextureAnimation resolveTextureAnimation(BlockBenchTexture texture, int frameCount) {
        if (frameCount > 1) {
            int time = texture.getFrameTime() * 50;
            boolean interpolate = texture.getFrameInterpolate();
            TextureAnimation.Mode mode = texture.getFrameMode();
            return new TextureAnimation(time, frameCount, mode, interpolate);
        }
        return TextureAnimation.EMPTY;
    }
}
