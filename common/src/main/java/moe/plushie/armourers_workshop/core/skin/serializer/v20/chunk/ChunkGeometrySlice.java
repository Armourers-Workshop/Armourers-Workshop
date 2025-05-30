package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometry;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.ChunkGeometrySerializer;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.ChunkGeometrySerializers;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.OpenSliceAccessor;

import java.util.BitSet;

public class ChunkGeometrySlice implements OpenSliceAccessor.Provider<SkinGeometry> {

    protected int readerIndex = 0;

    protected final int startIndex;
    protected final int endIndex;

    protected final int base;
    protected final int stride;

    protected final byte[] bytes;
    protected final ChunkPaletteData palette;

    protected final SkinGeometryType geometryType;
    protected final int geometryOptions;

    protected final BitSet flags = new BitSet();
    protected final ChunkGeometrySerializer.Decoder<?> decoder;

    public ChunkGeometrySlice(int startIndex, int endIndex, ChunkGeometrySelector selector, ChunkGeometrySection.Immutable section) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.base = selector.getIndex();
        this.stride = section.stride;
        this.geometryType = section.getGeometryType();
        this.geometryOptions = section.getGeometryOptions();

        this.bytes = section.getBytes();
        this.palette = section.getPalette();

        this.decoder = ChunkGeometrySerializers.createDecoder(geometryType, this);
    }

    public boolean once(int index) {
        if (flags.get(index)) {
            return false;
        }
        flags.set(index);
        return true;
    }

    @Override
    public SkinGeometry get(int index) {
        int newReaderIndex = stride * (base + index);
        if (readerIndex != newReaderIndex) {
            readerIndex = newReaderIndex;
            flags.clear();
        }
        return decoder.begin();
    }

    @Override
    public int getStartIndex() {
        return startIndex;
    }

    @Override
    public int getEndIndex() {
        return endIndex;
    }

    public ChunkPaletteData getPalette() {
        return palette;
    }

    public SkinGeometryType getGeometryType() {
        return geometryType;
    }

    public int getGeometryOptions() {
        return geometryOptions;
    }

    public byte getByte(int offset) {
        return bytes[readerIndex + offset];
    }

    public int getInt(int offset) {
        int ch1 = getByte(offset + 0) & 0xff;
        int ch2 = getByte(offset + 1) & 0xff;
        int ch3 = getByte(offset + 2) & 0xff;
        int ch4 = getByte(offset + 3) & 0xff;
        return ((ch1 << 24) | (ch2 << 16) | (ch3 << 8) | ch4);
    }

    public float getFloat(int offset) {
        return Float.intBitsToFloat(getInt(offset));
    }

    public int getFixedInt(int offset, int usedBytes) {
        if (usedBytes == 4) {
            return getInt(offset);
        }
        int value = 0;
        for (int i = 0; i < usedBytes; i++) {
            int ch = getByte(offset + i) & 0xff;
            value = (value << 8) | ch;
        }
        return value;
    }

    public float getFixedFloat(int offset, int usedBytes) {
        return Float.intBitsToFloat(getFixedInt(offset, usedBytes));
    }

    public OpenVector3f getVector3f(int offset) {
        float x = getFloat(offset);
        float y = getFloat(offset + 4);
        float z = getFloat(offset + 8);
        if (x == 0 && y == 0 && z == 0) {
            return OpenVector3f.ZERO;
        }
        if (x == 1 && y == 1 && z == 1) {
            return OpenVector3f.ONE;
        }
        return new OpenVector3f(x, y, z);
    }

    public OpenRectangle3f getRectangle3f(int offset) {
        float x = getFloat(offset);
        float y = getFloat(offset + 4);
        float z = getFloat(offset + 8);
        float width = getFloat(offset + 12);
        float height = getFloat(offset + 16);
        float depth = getFloat(offset + 20);
        return new OpenRectangle3f(x, y, z, width, height, depth);
    }


    public OpenTransform3f getTransform(int offset) {
        int flags = getInt(offset);
        var translate = getVector3f(offset + 4);
        var rotation = getVector3f(offset + 16);
        var scale = getVector3f(offset + 28);
        var afterTranslate = getVector3f(offset + 40);
        var pivot = getVector3f(offset + 52);
        return OpenTransform3f.create(translate, rotation, scale, pivot, afterTranslate);
    }

    public SkinPaintColor getColor(int offset) {
        return palette.readColor(getFixedInt(offset, palette.getColorIndexBytes()));
    }

    public OpenVector2f getTexturePos(int offset) {
        int usedBytes = palette.getTextureIndexBytes();
        float x = getFixedFloat(offset, usedBytes);
        float y = getFixedFloat(offset + usedBytes, usedBytes);
        if (x == 0 && y == 0) {
            return OpenVector2f.ZERO;
        }
        return new OpenVector2f(x, y);
    }

    public long getTextureOptions(int offset) {
        int usedBytes = palette.getTextureIndexBytes();
        int x = getFixedInt(offset, usedBytes);
        int y = getFixedInt(offset + usedBytes, usedBytes);
        return ((long) y << 32) | x;
    }
}
