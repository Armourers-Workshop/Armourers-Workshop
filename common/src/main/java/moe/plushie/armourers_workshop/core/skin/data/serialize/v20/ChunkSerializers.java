package moe.plushie.armourers_workshop.core.skin.data.serialize.v20;

import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV1;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataInputStream;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkContext;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkCubeData;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkInputStream;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkOutputStream;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkPaintData;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkPalette;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.ChunkType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.transform.SkinTransform;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;

/**
 * file format:           | header(4b) | version(4b) | reserved data(8b) | skin type |[ chunk ]| crc32 |
 * chunk format:          |< length(4b) | name(4b) | flag(2b) >[ chunk data ]|
 * skin properties :      | length | PPTS | flag |[ key/value ]|
 * color palette:         | length | PALE | flag |< paint type(1b) | bytes(1b) >[ RRGGBB/AARRGGBB ]]|
 * texture paint data:    | length | TPDT | flag | version(1b) | slim(1b) |< empty >[ color index ]|
 * cube data:             | length | CBDT | flag |< id(1b) >[ x(1b)/y(1b)/z(1b) |[ face(1b: 1|2|4|8|16|32) | color index ](1-6)]|
 * skin preview:          | length | SKPV | flag |< matrix(64b) >[ cube sel(8b) ]|
 * skin part:             | length | SKPR | flag | id(2b) | skin part type |[ chunk ]|
 * skin part cubes:       | length | PRCB | flag |< empty >[ cube sel(8b) ]|
 * skin part markers:     | length | PRMK | flag |< empty >[ x(1b)/y(1b)/z(1b) | meta(1b) ]|
 * skin part name         | length | PRNM | flag | part name |
 * skin part parent:      | length | PRPP | flag | parent part id(2b) |
 * skin part transform:   | length | PRTF | flag | postTranslate/postRotation/postScale/preTranslate/preRotation/preScale |
 * chunk flag:            1 encrypt, 2 gzip, 3 encrypt+gzip
 * file header:           0x534b494e (SKIN)
 */
public class ChunkSerializers {

    public static final ChunkSerializer<Skin, Void> SKIN = register(new ChunkSerializer<Skin, Void>(ChunkType.SKIN) {

        @Override
        public Skin read(ChunkInputStream stream, Void obj) throws IOException {
            ISkinType skinType = stream.readType(SkinTypes::byName);
            return stream.readChunk(it -> {
                ChunkPalette palette = it.read(SKIN_COLOR_PALETTE);
                Skin.Builder builder = new Skin.Builder(skinType);
                builder.properties(it.read(SKIN_PROPERTIES));
                builder.paintData(it.read(SKIN_PAINT_DATA, palette));
                builder.parts(it.readAll(SKIN_PART, palette));
                builder.blobs(it.readBlobs());
                return builder.build();
            });
        }

        @Override
        public void write(Skin skin, Void obj, ChunkOutputStream stream) throws IOException {
            stream.writeType(skin.getType());
            stream.writeChunk(it -> {
                ChunkPalette palette = new ChunkPalette();
                it.write(SKIN_PROPERTIES, skin.getProperties());
                it.write(SKIN_COLOR_PALETTE, palette);
                it.write(SKIN_PAINT_DATA, skin.getPaintData(), palette);
                for (SkinPart part : skin.getParts()) {
                    it.write(SKIN_PART, part, palette);
                }
                it.writeBlobs(skin.getBlobs());
            });
        }
    });

    public static final ChunkSerializer<Pair<ISkinType, ISkinProperties>, Void> SKIN_INFO = register(new ChunkSerializer<Pair<ISkinType, ISkinProperties>, Void>(ChunkType.SKIN) {

        @Override
        public Pair<ISkinType, ISkinProperties> read(ChunkInputStream stream, Void obj) throws IOException {
            ISkinType skinType = stream.readType(SkinTypes::byName);
            return stream.readChunk(it -> {
                ISkinProperties properties = it.read(SKIN_PROPERTIES);
                return Pair.of(skinType, properties);
            });
        }

        @Override
        public void write(Pair<ISkinType, ISkinProperties> info, Void obj, ChunkOutputStream stream) throws IOException {
        }
    });

    public static final ChunkSerializer<SkinPart, ChunkPalette> SKIN_PART = register(new ChunkSerializer<SkinPart, ChunkPalette>(ChunkType.SKIN_PART) {

        @Override
        public SkinPart read(ChunkInputStream stream, ChunkPalette palette) throws IOException {
            int id = stream.readShort();
            ISkinPartType partType = stream.readType(SkinPartTypes::byName);
            return stream.readChunk(it -> {
                SkinPart.Builder builder = new SkinPart.Builder(partType);
                builder.id(id);
                builder.parent(it.read(SKIN_PART_PARENT));
                builder.name(it.read(SKIN_PART_NAME));
                builder.transform(it.read(SKIN_TRANSFORM));
                builder.cubes(it.read(SKIN_CUBE, palette));
                builder.paintData(it.read(SKIN_PAINT_DATA, palette));
                builder.markers(it.read(SKIN_MARKERS));
                // TODO: impl @SAGESSE
//                builder.properties(it.read(SKIN_PROPERTIES));
                builder.blobs(it.readBlobs());
                return builder.build();
            });
        }

        @Override
        public void write(SkinPart part, ChunkPalette palette, ChunkOutputStream stream) throws IOException {
            stream.writeShort(0);
            stream.writeType(part.getType());
            stream.writeChunk(it -> {
                it.write(SKIN_PART_NAME, part.getName());
                it.write(SKIN_PART_PARENT, 0);
                it.write(SKIN_TRANSFORM, part.getTransform());
                it.write(SKIN_CUBE, part.getCubeData(), palette);
                it.write(SKIN_PAINT_DATA, part.getPaintData(), palette);
                it.write(SKIN_MARKERS, part.getMarkers());
                // TODO: impl @SAGESSE
//                it.write(SKIN_PROPERTIES, part.getProperties());
                it.writeBlobs(part.getBlobs());
            });
        }
    });

    public static final ChunkSerializer<String, Void> SKIN_PART_NAME = register(new ChunkSerializer<String, Void>(ChunkType.SKIN_PART_NAME) {

        @Override
        public String read(ChunkInputStream stream, Void obj) throws IOException {
            return stream.readString();
        }

        @Override
        public void write(String value, Void obj, ChunkOutputStream stream) throws IOException {
            stream.writeString(value);
        }
    });
    public static final ChunkSerializer<Integer, Void> SKIN_PART_PARENT = register(new ChunkSerializer<Integer, Void>(ChunkType.SKIN_PART_PARENT) {

        @Override
        public Integer read(ChunkInputStream stream, Void obj) throws IOException {
            return (int) stream.readShort();
        }

        @Override
        public void write(Integer value, Void obj, ChunkOutputStream stream) throws IOException {
            stream.writeShort(value.shortValue());
        }
    });

    public static final ChunkSerializer<SkinCubes, ChunkPalette> SKIN_CUBE = register(new ChunkSerializer<SkinCubes, ChunkPalette>(ChunkType.CUBE_DATA) {

        @Override
        public SkinCubes read(ChunkInputStream stream, ChunkPalette palette) throws IOException {
            SkinCubes cubes = new SkinCubesV1(0);
            ChunkCubeData cubeData = new ChunkCubeData(palette, cubes);
            cubeData.readFromStream(stream);
            return cubes;
        }

        @Override
        public void write(SkinCubes value, ChunkPalette palette, ChunkOutputStream stream) throws IOException {
            ChunkCubeData chunkCubeData = new ChunkCubeData(palette, value);
            chunkCubeData.writeToStream(stream);
        }
    });

    public static final ChunkSerializer<ArrayList<SkinMarker>, Void> SKIN_MARKERS = register(new ChunkSerializer<ArrayList<SkinMarker>, Void>(ChunkType.MARKER) {

        @Override
        public ArrayList<SkinMarker> read(ChunkInputStream stream, Void obj) throws IOException {
            int size = stream.readInt();
            ArrayList<SkinMarker> markers = new ArrayList<>();
            for (int i = 0; i < size; ++i) {
                markers.add(new SkinMarker(stream));
            }
            return markers;
        }

        @Override
        public void write(ArrayList<SkinMarker> value, Void obj, ChunkOutputStream stream) throws IOException {
            stream.writeInt(value.size());
            for (SkinMarker marker : value) {
                marker.writeToStream(stream);
            }
        }
    });

    public static final ChunkSerializer<SkinPaintData, ChunkPalette> SKIN_PAINT_DATA = register(new ChunkSerializer<SkinPaintData, ChunkPalette>(ChunkType.PAINT_DATA) {

        @Override
        public SkinPaintData read(ChunkInputStream stream, ChunkPalette palette) throws IOException {
            ChunkPaintData chunkPaintData = new ChunkPaintData(palette);
            return chunkPaintData.readFromStream(stream);
        }

        @Override
        public void write(SkinPaintData value, ChunkPalette palette, ChunkOutputStream stream) throws IOException {
            ChunkPaintData chunkPaintData = new ChunkPaintData(palette);
            chunkPaintData.writeToStream(value, stream);
        }
    });

    public static final ChunkSerializer<SkinProperties, Void> SKIN_PROPERTIES = register(new ChunkSerializer<SkinProperties, Void>(ChunkType.PROPERTIES) {

        @Override
        public SkinProperties read(ChunkInputStream stream, Void obj) throws IOException {
            SkinProperties properties = new SkinProperties();
            properties.readFromStream(stream);
            return properties;
        }

        @Override
        public void write(SkinProperties value, Void obj, ChunkOutputStream stream) throws IOException {
            value.writeToStream(stream);
        }
    });

    public static final ChunkSerializer<SkinTransform, Void> SKIN_TRANSFORM = register(new ChunkSerializer<SkinTransform, Void>(ChunkType.TRANSFORM) {

        @Override
        public SkinTransform read(ChunkInputStream stream, Void obj) throws IOException {
            SkinTransform transform = new SkinTransform();
            transform.readFromStream(stream);
            if (transform.equals(SkinTransform.IDENTIFIER)) {
                transform = SkinTransform.IDENTIFIER;
            }
            return transform;
        }

        @Override
        public void write(SkinTransform value, Void obj, ChunkOutputStream stream) throws IOException {
            value.writeToStream(stream);
        }

        @Override
        public boolean isChunkEmpty(SkinTransform value) {
            return value == null || value.equals(SkinTransform.IDENTIFIER);
        }
    });

    public static final ChunkSerializer<ChunkPalette, Void> SKIN_COLOR_PALETTE = register(new ChunkSerializer<ChunkPalette, Void>(ChunkType.PALETTE) {

        @Override
        public ChunkPalette read(ChunkInputStream stream, Void obj) throws IOException {
            ChunkPalette palette = new ChunkPalette();
            palette.readFromStream(stream);
            return palette;
        }

        @Override
        public void write(ChunkPalette value, Void obj, ChunkOutputStream stream) throws IOException {
            stream.writeVariable(value);
        }
    });


    private static <T, C> ChunkSerializer<T, C> register(ChunkSerializer<T, C> serializer) {
        return serializer;
    }

    public static void writeToStream(Skin skin, IDataOutputStream stream) throws IOException {
        ChunkOutputStream stream1 = new ChunkOutputStream(stream.getOutputStream(), new ChunkContext());
        SKIN.write(skin, null, stream1);
        stream1.flush();
    }

    public static Skin readFromStream(IDataInputStream stream) throws IOException {
        ChunkInputStream stream1 = new ChunkInputStream(stream.getInputStream(), new ChunkContext(), null);
        return SKIN.read(stream1, null);
    }

    public static Pair<ISkinType, ISkinProperties> readInfoFromStream(IDataInputStream stream) throws IOException {
        ArrayList<String> allows = Lists.newArrayList(ChunkType.PROPERTIES.getName());
        ChunkInputStream stream1 = new ChunkInputStream(stream.getInputStream(), new ChunkContext(), allows::contains);
        return SKIN_INFO.read(stream1, null);
    }
}
