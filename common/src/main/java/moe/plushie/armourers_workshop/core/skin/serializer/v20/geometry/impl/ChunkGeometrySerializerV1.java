package moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.impl;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkGeometrySlice;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.ChunkGeometrySerializer;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * geometry entry (v1):   | x(1B)/y(1B)/z(1B) |[ face options(1B) | color index(VB) ](1-6)]|
 */
public class ChunkGeometrySerializerV1 extends ChunkGeometrySerializer {

    @Override
    public int stride(SkinGeometryType geometryType, int options, ChunkPaletteData palette) {
        int faceCount = options & 0x0F;
        return Decoder.calcStride(palette.getColorIndexBytes(), faceCount);
    }

    @Override
    public ChunkGeometrySerializer.Encoder<?> encoder(SkinGeometryType geometryType) {
        return new Encoder();
    }

    @Override
    public ChunkGeometrySerializer.Decoder<?> decoder(SkinGeometryType geometryType, ChunkGeometrySlice slice) {
        return new Decoder(geometryType, slice);
    }

    protected static class Decoder extends SkinCube implements ChunkGeometrySerializer.Decoder<SkinCube> {

        private final int faceCount;

        private final SkinGeometryType type;
        private final ChunkGeometrySlice slice;
        private final ChunkPaletteData palette;

        public Decoder(SkinGeometryType type, ChunkGeometrySlice slice) {
            this.type = type;
            this.palette = slice.getPalette();
            this.slice = slice;
            this.faceCount = slice.getGeometryOptions() & 0x0F;
        }

        public static int calcStride(int usedBytes, int size) {
            // x/y/z (face + color ref) * faceCount
            return 3 + (1 + usedBytes) * size;
        }

        @Override
        public SkinCube begin() {
            return this;
        }

        @Override
        public SkinGeometryType getType() {
            return type;
        }

        @Override
        public OpenRectangle3f getBoundingBox() {
            if (slice.once(0)) {
                float x = slice.getByte(0);
                float y = slice.getByte(1);
                float z = slice.getByte(2);
                boundingBox = new OpenRectangle3f(x, y, z, 1, 1, 1);
            }
            return boundingBox;
        }

        @Override
        public SkinPaintColor getPaintColor(OpenDirection dir) {
            if (slice.once(1)) {
                parseColors();
            }
            return super.getPaintColor(dir);
        }

        @Override
        public SkinTexturePos getTexture(OpenDirection dir) {
            return null;
        }

        protected void parseColors() {
            int usedBytes = palette.getColorIndexBytes();
            for (int i = 0; i < faceCount; ++i) {
                int face = slice.getByte(calcStride(usedBytes, i));
                var color = slice.getColor(calcStride(usedBytes, i) + 1);
                for (var dir1 : OpenDirection.valuesFromSet(face)) {
                    paintColors.put(dir1, color);
                }
            }
        }
    }

    protected static class Encoder implements ChunkGeometrySerializer.Encoder<SkinCube> {

        private OpenVector3i pos = OpenVector3i.ZERO;
        private final LinkedHashMap<SkinPaintColor, Integer> values = new LinkedHashMap<>();

        @Override
        public int begin(SkinCube cube) {
            // merge all values
            pos = cube.getBlockPos();
            for (var dir : OpenDirection.values()) {
                var value = cube.getPaintColor(dir);
                int face = values.getOrDefault(value, 0);
                face |= 1 << dir.get3DDataValue();
                values.put(value, face);
            }
            return values.size();
        }

        @Override
        public void end(ChunkPaletteData palette, ChunkDataOutputStream stream) throws IOException {
            // position(3B)
            stream.writeByte(pos.x());
            stream.writeByte(pos.y());
            stream.writeByte(pos.z());

            // face: <color ref>
            for (var entry : values.entrySet()) {
                stream.writeByte(entry.getValue());
                stream.writeVariable(palette.writeColor(entry.getKey()));
            }

            values.clear();
        }
    }
}
