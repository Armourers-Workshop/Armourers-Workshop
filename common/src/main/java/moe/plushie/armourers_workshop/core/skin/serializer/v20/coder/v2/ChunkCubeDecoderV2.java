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
import moe.plushie.armourers_workshop.utils.texture.TextureBox;
import moe.plushie.armourers_workshop.utils.texture.TextureKey;
import net.minecraft.core.Direction;

import java.util.BitSet;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;

import manifold.ext.rt.api.auto;

public class ChunkCubeDecoderV2 extends ChunkCubeDecoder {

    private Rectangle3f shape = Rectangle3f.ZERO;
    private SkinTransform transform = SkinTransform.IDENTITY;

    protected final BitSet flags = new BitSet();
    protected final AtomicInteger index = new AtomicInteger(0);
    protected final EnumMap<Direction, Vector2f> startUVs = new EnumMap<>(Direction.class);
    protected final EnumMap<Direction, Vector2f> endUVs = new EnumMap<>(Direction.class);
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
            index.set(0);
            float x = getFloat(index.getAndAdd(4));
            float y = getFloat(index.getAndAdd(4));
            float z = getFloat(index.getAndAdd(4));
            float width = getFloat(index.getAndAdd(4));
            float height = getFloat(index.getAndAdd(4));
            float depth = getFloat(index.getAndAdd(4));
            shape = new Rectangle3f(x, y, z, width, height, depth);
        }
        return shape;
    }

    @Override
    public SkinTransform getTransform() {
        if (setBit(1)) {
            index.set(24);
            int flags = getInt(index.getAndAdd(4));
            Vector3f translate = getVector3f(index.getAndAdd(12));
            Vector3f rotation = getVector3f(index.getAndAdd(12));
            Vector3f scale = getVector3f(index.getAndAdd(12));
            Vector3f vector = getVector3f(index.getAndAdd(12));
            Vector3f pivot = getVector3f(index.getAndAdd(12));
            transform = SkinTransform.create(translate, rotation, scale, pivot, vector);
        }
        return transform;
    }

    @Override
    public IPaintColor getPaintColor(Direction dir) {
        ITextureKey key = getTexture(dir);
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
        textureKeys.clear();
        TextureBox textureBox = null;
        int usedBytes = palette.getTextureIndexBytes();
        for (int i = 0; i < faceCount; ++i) {
            int face = getByte(calcStride(usedBytes, i));
            auto pos = ChunkColorSection.TextureRef.readFromStream(usedBytes, readerIndex + calcStride(usedBytes, i) + 1, bytes);
            for (Direction dir : DirectionUtils.valuesFromSet(face)) {
                endUVs.put(dir, pos);
                if (!startUVs.containsKey(dir)) {
                    startUVs.put(dir, pos);
                }
            }
            if ((face & 0x80) != 0) {
                auto ref = palette.readTexture(pos);
                if (ref == null) {
                    continue;
                }
                auto shape = getShape();
                float width = shape.getWidth();
                float height = shape.getHeight();
                float depth = shape.getDepth();
                textureBox = new TextureBox(width, height, depth, false, ref.getPos(), ref.getProvider());
            }
        }
        for (Direction dir : Direction.values()) {
            Vector2f start = startUVs.get(dir);
            Vector2f end = endUVs.get(dir);
            if (start != null && end != null) {
                auto ref = palette.readTexture(start);
                if (ref == null) {
                    continue;
                }
                float u = ref.getU();
                float v = ref.getV();
                float width = end.getX() - start.getX();
                float height = end.getY() - start.getY();
                textureKeys.put(dir, new TextureKey(u, v, width, height, ref.getProvider()));
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