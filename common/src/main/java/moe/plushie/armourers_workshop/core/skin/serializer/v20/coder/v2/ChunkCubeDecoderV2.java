package moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.v2;

import moe.plushie.armourers_workshop.api.common.ITextureKey;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.face.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkColorSection;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkCubeSection;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkCubeSelector;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeDecoder;
import moe.plushie.armourers_workshop.utils.DirectionUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector2f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.core.texture.TextureBox;
import moe.plushie.armourers_workshop.core.texture.TextureKey;
import moe.plushie.armourers_workshop.core.texture.TextureOptions;
import net.minecraft.core.Direction;

import java.util.BitSet;
import java.util.EnumMap;

public class ChunkCubeDecoderV2 extends ChunkCubeDecoder {

    private Rectangle3f shape = Rectangle3f.ZERO;
    private SkinTransform transform = SkinTransform.IDENTITY;

    protected final BitSet flags = new BitSet();
    protected final EnumMap<Direction, Vector2f> startUVs = new EnumMap<>(Direction.class);
    protected final EnumMap<Direction, Vector2f> endUVs = new EnumMap<>(Direction.class);
    protected final EnumMap<Direction, TextureOptions> optionsValues = new EnumMap<>(Direction.class);
    protected final EnumMap<Direction, ITextureKey> textureKeys = new EnumMap<>(Direction.class);

    public ChunkCubeDecoderV2(int startIndex, int endIndex, ChunkCubeSelector selector, ChunkCubeSection.Immutable section) {
        super(startIndex, endIndex, selector, section);
    }

    public static int getStride(int options, ChunkPaletteData palette) {
        int faceCount = options & 0x0F;
        return calcStride(palette.getTextureIndexBytes(), faceCount);
    }

    public static int calcStride(int usedBytes, int size) {
        // rectangle(24B) + transform(64b) + (face flag + texture ref) * faceCount;
        return Rectangle3f.BYTES + SkinTransform.BYTES + (1 + usedBytes * 2) * size;
    }

    @Override
    public Rectangle3f getShape() {
        if (setBit(0)) {
            float x = getFloat(0);
            float y = getFloat(4);
            float z = getFloat(8);
            float width = getFloat(12);
            float height = getFloat(16);
            float depth = getFloat(20);
            shape = new Rectangle3f(x, y, z, width, height, depth);
        }
        return shape;
    }

    @Override
    public SkinTransform getTransform() {
        if (setBit(1)) {
            int flags = getInt(24);
            Vector3f translate = getVector3f(28);
            Vector3f rotation = getVector3f(40);
            Vector3f scale = getVector3f(52);
            Vector3f vector = getVector3f(64);
            Vector3f pivot = getVector3f(76);
            transform = SkinTransform.create(translate, rotation, scale, pivot, vector);
        }
        return transform;
    }

    @Override
    public IPaintColor getPaintColor(Direction dir) {
        var key = getTexture(dir);
        if (key != null) {
            return PaintColor.WHITE;
        }
        return PaintColor.CLEAR;
    }

    @Override
    public ITextureKey getTexture(Direction dir) {
        if (setBit(2)) {
            parseTextures();
        }
        return textureKeys.get(dir);
    }


    @Override
    public SkinCubeFace getFace(Direction dir) {
        if (getTexture(dir) != null) {
            return super.getFace(dir);
        }
        return null;
    }

    protected void parseTextures() {
        startUVs.clear();
        endUVs.clear();
        optionsValues.clear();
        textureKeys.clear();
        TextureBox textureBox = null;
        int usedBytes = palette.getTextureIndexBytes();
        for (int i = 0; i < faceCount; ++i) {
            int index = calcStride(usedBytes, i);
            int face = getByte(index);
            if ((face & 0x40) != 0) {
                var opt = ChunkColorSection.OptionsRef.readFromStream(usedBytes, readerIndex + index + 1, bytes);
                for (Direction dir : DirectionUtils.valuesFromSet(face)) {
                    optionsValues.put(dir, opt);
                }
                continue;
            }
            var pos = ChunkColorSection.TextureRef.readFromStream(usedBytes, readerIndex + index + 1, bytes);
            for (Direction dir : DirectionUtils.valuesFromSet(face)) {
                endUVs.put(dir, pos);
                if (!startUVs.containsKey(dir)) {
                    startUVs.put(dir, pos);
                }
            }
            if ((face & 0x80) != 0) {
                var ref = palette.readTexture(pos);
                if (ref == null) {
                    continue;
                }
                var shape = getShape();
                float width = shape.getWidth();
                float height = shape.getHeight();
                float depth = shape.getDepth();
                textureBox = new TextureBox(width, height, depth, false, ref.getPos(), ref.getProvider());
            }
        }
        for (var dir : Direction.values()) {
            var start = startUVs.get(dir);
            var end = endUVs.get(dir);
            if (start != null && end != null) {
                var opt = optionsValues.get(dir);
                var ref = palette.readTexture(start);
                if (ref == null) {
                    continue;
                }
                float u = ref.getU();
                float v = ref.getV();
                float width = end.getX() - start.getX();
                float height = end.getY() - start.getY();
                textureKeys.put(dir, new TextureKey(u, v, width, height, opt, ref.getProvider()));
            } else if (textureBox != null) {
                textureKeys.put(dir, textureBox.getTexture(dir));
            }
        }
    }

    @Override
    protected void reset() {
        flags.clear();
    }

    protected boolean setBit(int index) {
        if (flags.get(index)) {
            return false;
        }
        flags.set(index);
        return true;
    }
}
