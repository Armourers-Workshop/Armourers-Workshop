package moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.impl;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryOptions;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer2;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkGeometrySlice;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.ChunkGeometrySerializer;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureBox;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureOptions;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedHashMap;

/**
 * geometry entry (v2):   | origin(12B)/size(12B) | type(4b)/translate(12B)/rotation(12B)/scale(12B)/pivot(12B)/offset(12B) |[ face options(1B) | u(VB)/v(VB):first or s(VB)/t(VB):second(optional) ]|
 */
public class ChunkGeometrySerializerV2 extends ChunkGeometrySerializer {

    @Override
    public int stride(SkinGeometryType geometryType, int options, ChunkPaletteData palette) {
        int faceCount = options & 0x0F;
        return Decoder.calcStride(palette.textureIndexBytes(), faceCount);
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

        private final EnumMap<OpenDirection, OpenVector2f> startUVs = new EnumMap<>(OpenDirection.class);
        private final EnumMap<OpenDirection, OpenVector2f> endUVs = new EnumMap<>(OpenDirection.class);
        private final EnumMap<OpenDirection, SkinTextureOptions> optionsValues = new EnumMap<>(OpenDirection.class);
        private final EnumMap<OpenDirection, SkinTexturePos> texturePoss = new EnumMap<>(OpenDirection.class);

        private OpenTransform3f transform = OpenTransform3f.IDENTITY;

        public Decoder(SkinGeometryType type, ChunkGeometrySlice slice) {
            this.type = type;
            this.palette = slice.palette();
            this.slice = slice;
            this.faceCount = slice.geometryOptions() & 0x0F;
        }

        public static int calcStride(int usedBytes, int size) {
            // rectangle(24B) + transform(64b) + (face flag + texture ref) * faceCount;
            return OpenRectangle3f.BYTES + OpenTransform3f.BYTES + (1 + usedBytes * 2) * size;
        }

        @Override
        public SkinCube begin() {
            return this;
        }

        @Override
        public SkinGeometryType type() {
            return type;
        }

        @Override
        public OpenRectangle3f boundingBox() {
            if (slice.once(0)) {
                boundingBox = slice.getRectangle3f(0);
            }
            return boundingBox;
        }

        @Override
        public OpenTransform3f transform() {
            if (slice.once(1)) {
                transform = slice.getTransform(24);
            }
            return transform;
        }

        @Override
        public SkinPaintColor getPaintColor(OpenDirection dir) {
            var key = getTexture(dir);
            if (key != null) {
                return SkinPaintColor.WHITE;
            }
            return SkinPaintColor.CLEAR;
        }

        @Override
        public SkinTexturePos getTexture(OpenDirection dir) {
            if (slice.once(2)) {
                parseTextures();
            }
            return texturePoss.get(dir);
        }


        @Override
        public SkinCubeFace getFace(OpenDirection dir) {
            if (getTexture(dir) != null) {
                return super.getFace(dir);
            }
            return null;
        }

        protected void parseTextures() {
            // dir: texture pos.
            // dir | 0x80: sky box.
            // dir | 0x40: texture options .
            // 0xff: cube options.
            startUVs.clear();
            endUVs.clear();
            optionsValues.clear();
            texturePoss.clear();
            options = SkinGeometryOptions.EMPTY;
            SkinTextureBox textureBox = null;
            int usedBytes = palette.textureIndexBytes();
            for (int i = 0; i < faceCount; ++i) {
                int index = calcStride(usedBytes, i);
                int face = slice.getByte(index);
                // a cube options.
                if ((face & 0xff) == 0xff) {
                    options = new SkinGeometryOptions(slice.getTextureOptions(index + 1));
                    continue;
                }
                // a texture options.
                if ((face & 0x40) != 0) {
                    var opt = new SkinTextureOptions(slice.getTextureOptions(index + 1));
                    for (var dir : OpenDirection.valuesFromSet(face)) {
                        optionsValues.put(dir, opt);
                    }
                    continue;
                }
                var pos = slice.getTexturePos(index + 1);
                for (var dir : OpenDirection.valuesFromSet(face)) {
                    endUVs.put(dir, pos);
                    if (!startUVs.containsKey(dir)) {
                        startUVs.put(dir, pos);
                    }
                }
                // a sky texture pos.
                if ((face & 0x80) != 0) {
                    var ref = palette.readTexture(pos);
                    if (ref == null) {
                        continue;
                    }
                    var rect = boundingBox();
                    float width = rect.width();
                    float height = rect.height();
                    float depth = rect.depth();
                    textureBox = new SkinTextureBox(width, height, depth, false, ref.uv(), ref.data());
                }
            }
            for (var dir : OpenDirection.values()) {
                var start = startUVs.get(dir);
                var end = endUVs.get(dir);
                if (start != null && end != null) {
                    var opt = optionsValues.get(dir);
                    var ref = palette.readTexture(start);
                    if (ref == null) {
                        continue;
                    }
                    float u = ref.u();
                    float v = ref.v();
                    float width = end.x() - start.x();
                    float height = end.y() - start.y();
                    texturePoss.put(dir, new SkinTexturePos(u, v, width, height, opt, ref.data()));
                } else if (textureBox != null) {
                    texturePoss.put(dir, textureBox.getTexture(dir));
                }
            }
        }
    }

    protected static class Encoder implements ChunkGeometrySerializer.Encoder<SkinCube> {

        private OpenRectangle3f boundingBox = OpenRectangle3f.ZERO;
        private SkinGeometryOptions options = SkinGeometryOptions.EMPTY;
        private OpenTransform3f transform = OpenTransform3f.IDENTITY;

        private final SortedMap<OpenVector2f> startValues = new SortedMap<>();
        private final SortedMap<OpenVector2f> endValues = new SortedMap<>();
        private final SortedMap<SkinTextureOptions> optionsValues = new SortedMap<>();

        @Override
        public int begin(SkinCube geometry) {
            // merge all values
            for (var dir : OpenDirection.values()) {
                var value = geometry.getTexture(dir);
                if (value == null) {
                    continue;
                }
                var provider = value.data();
                if (value instanceof SkinTextureBox.Entry entry) {
                    startValues.put(0x80, entry.parent(), provider);
                    // box need options?
                    continue;
                }
                int face = 1 << dir.get3DDataValue();
                float u = value.u();
                float v = value.v();
                float s = value.width();
                float t = value.height();
                startValues.put(face, new OpenVector2f(u, v), provider);
                endValues.put(face, new OpenVector2f(u + s, v + t), provider);
                if (value.options() != null) {
                    optionsValues.put(face, value.options(), provider);
                }
            }
            options = geometry.options();
            transform = geometry.transform();
            boundingBox = geometry.boundingBox();
            return estimatedTotal();
        }

        @Override
        public void end(ChunkPaletteData palette, ChunkDataOutputStream stream) throws IOException {
            // rectangle(24B) + transform(64b)
            stream.writeRectangle3f(boundingBox);
            stream.writeTransformf(transform);

            // geometry options(8b).
            if (!options.isEmpty()) {
                stream.writeByte(0xff);
                stream.writeVariable(palette.writeTextureOptions(options.asLong()));
            }

            // face: <texture ref>
            optionsValues.forEach((key, value) -> {
                stream.writeByte(0x40 | value);
                stream.writeVariable(palette.writeTextureOptions(key.getKey().asLong()));
            });
            startValues.forEach((key, value) -> {
                stream.writeByte(value);
                stream.writeVariable(palette.writeTexture(key.getKey(), key.getValue()));
            });
            endValues.forEach((key, value) -> {
                stream.writeByte(value);
                stream.writeVariable(palette.writeTexture(key.getKey(), key.getValue()));
            });

            startValues.clear();
            endValues.clear();
            optionsValues.clear();
        }

        protected int estimatedTotal() {
            int total = startValues.size() + endValues.size() + optionsValues.size();
            if (!options.isEmpty()) {
                return total + 1; // a geometry options.
            }
            return total;
        }
    }

    protected static class SortedMap<T> {

        private final LinkedHashMap<Pair<T, SkinTextureData>, Integer> impl = new LinkedHashMap<>();

        public void forEach(IOConsumer2<Pair<T, SkinTextureData>, Integer> consumer) throws IOException {
            for (var entry : impl.entrySet()) {
                consumer.accept(entry.getKey(), entry.getValue());
            }
        }

        public void put(int face, T pos, SkinTextureData textureData) {
            var index = Pair.of(pos, textureData);
            int newFace = impl.getOrDefault(index, 0);
            newFace |= face;
            impl.put(index, newFace);
        }

        public void clear() {
            impl.clear();
        }

        public int size() {
            return impl.size();
        }
    }
}
