package moe.plushie.armourers_workshop.core.skin.serializer.v20;

import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinSettings;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkContext;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkCubeData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaintData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPartData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPreviewData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkType;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import moe.plushie.armourers_workshop.utils.texture.SkinPreviewData;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * file header:           0x534b494e (SKIN)
 * file format:           | header(4B) | version(4B) | reserved data(8B) | skin type |{ chunk }| crc32 |
 * chunk format:          |< length(4B) | name(4B) | flag(2B) >[ chunk data ]|
 * part chunk format:     |< length(4B) | name(4B) | flag(2B) | id(4B) >[ chunk data ]|
 * skin properties :      | length | PPTS | flag |[ key/value ]|
 * cube data:             | length | CCBO | flag |< id(VB) | opt(VB) >[ cube entry(VB) |[ face options(1B) | cube face entry(VB) ](1-6)]|
 * cube data preview:     | length | VCBO | flag |< id(VB) | transform(VB) >[ cube sel(8B) ]|
 * skin part:             | length | SKPR | flag |[ id(VB) | pid(VB) | name(VB) | type(VB) | transform(VB) |[ cube sel(8B) ]]|{ part chunk }|
 * skin part markers:     | length | PRMK | flag |[ x(1B)/y(1B)/z(1B) | meta(1B) ]|
 * skin paint data:       | length | PADT | flag | opt(VB)/total width(VB)/total height(VB) |< width(VB) | height(VB) >[ color index ]|
 * palette data:          | length | PALE | flag | opt(VB)/reserved(VB) |< paint type(1B)/used bytes(1B) >[ palette entry(VB) ]|
 * chunk flag:            1 encrypt, 2 gzip, 3 encrypt+gzip
 * cube entry:            x(1B)/y(1B)/z(1B)
 *                        origin(12B)/size(12B), type(4b)/translate(12B)/rotation(12B)/scale(12B)/pivot(12B)/offset(12B)
 * cube face entry:       color index(VB)
 *                        first: u(VB)/v(VB), second: s(VB)/t(VB)
 * palette entry:         RRGGBB/AARRGGBB
 *                        id(VB)/parent id(VB)/x(4B)/y(4B)/w(4B)/h(4B)/ani(4B)/opt(4B)/bytes(4B) | raw data(nB)
 * symbol:                {n} = (length(4B) + byte[length]) * n + 0(4B)
 *                        [data] = count(VB) + data[count]
 *                        <header>[data] = (count(VB) + header + data[count]) * n + 0(VB)
 */
@SuppressWarnings("unused")
public class ChunkSerializers {

    public static final ChunkSerializer<Skin, Void> SKIN = register(new ChunkSerializer<Skin, Void>(ChunkType.SKIN) {

        @Override
        public Skin read(ChunkInputStream stream, String name, Void obj) throws IOException {
            int version = stream.getContext().getVersion();
            ISkinType skinType = stream.readType(SkinTypes::byName);
            return stream.readChunk(it -> {
                ChunkPaletteData palette = it.read(SKIN_TEXTURE_DATA);
                ChunkCubeData chunkCubes = it.read(SKIN_CUBE_DATA, palette);
                Skin.Builder builder = new Skin.Builder(skinType);
                builder.properties(it.read(SKIN_PROPERTIES));
                builder.settings(it.read(SKIN_SETTINGS));
                builder.paintData(it.read(SKIN_PAINT_DATA, palette));
                builder.previewData(it.read(SKIN_PREVIEW_DATA, chunkCubes));
                builder.parts(it.read(SKIN_PART, chunkCubes));
                builder.blobs(it.readBlobs());
                builder.version(version);
                return builder.build();
            });
        }

        @Override
        public void write(Skin skin, Void obj, ChunkOutputStream stream) throws IOException {
            ChunkContext context = stream.getContext();
            stream.writeType(skin.getType());
            stream.writeChunk(it -> {
                ChunkPaletteData palette = new ChunkPaletteData();
                ChunkCubeData chunkCubes = new ChunkCubeData(palette);
                it.write(SKIN_PROPERTIES, skin.getProperties());
                it.write(SKIN_SETTINGS, skin.getSettings());
                it.write(SKIN_TEXTURE_DATA, palette);
                it.write(SKIN_PAINT_DATA, skin.getPaintData(), palette);
                it.write(SKIN_CUBE_DATA, chunkCubes, palette);
                // whether to enable part data by the options.
                if (context.isEnablePartData()) {
                    it.write(SKIN_PART, skin.getParts(), chunkCubes);
                }
                // whether to enable preview data by the options.
                if (context.isEnablePreviewData()) {
                    it.write(SKIN_PREVIEW_DATA, SkinPreviewData.of(skin), chunkCubes);
                }
                it.writeBlobs(skin.getBlobs());
            });
        }
    });

    public static final ChunkSerializer<Pair<ISkinType, ISkinProperties>, Void> SKIN_INFO = register(new ChunkSerializer<Pair<ISkinType, ISkinProperties>, Void>(ChunkType.SKIN) {

        @Override
        public Pair<ISkinType, ISkinProperties> read(ChunkInputStream stream, String name, Void obj) throws IOException {
            ISkinType skinType = stream.readType(SkinTypes::byName);
            return stream.readChunk(it -> {
                ISkinProperties properties = it.read(SKIN_PROPERTIES);
                return Pair.of(skinType, properties);
            });
        }

        @Override
        public void write(Pair<ISkinType, ISkinProperties> info, Void obj, ChunkOutputStream stream) throws IOException {
            // we never call write method!!!
        }
    });

    public static final ChunkSerializer<Collection<SkinPart>, ChunkCubeData> SKIN_PART = register(new ChunkSerializer<Collection<SkinPart>, ChunkCubeData>(ChunkType.SKIN_PART) {

        @Override
        public Collection<SkinPart> read(ChunkInputStream stream, String name, ChunkCubeData chunkCubes) throws IOException {
            ChunkPartData partData = new ChunkPartData(chunkCubes);
            return partData.readFromStream(stream, (it, builder) -> {
                builder.markers(it.read(SKIN_MARKERS));
                // TODO: impl @SAGESSE
//                builder.paintData(it.read(SKIN_PAINT_DATA, palette));
//                builder.properties(it.read(SKIN_PROPERTIES));
                builder.blobs(it.readBlobs());
            });
        }

        @Override
        public void write(Collection<SkinPart> parts, ChunkCubeData chunkCubes, ChunkOutputStream stream) throws IOException {
            ChunkPartData partData = new ChunkPartData(chunkCubes);
            partData.writeToStream(stream, parts, (it, part) -> {
                it.write(SKIN_MARKERS, part.getMarkers());
                // TODO: impl @SAGESSE
//                it.write(SKIN_PAINT_DATA, part.getPaintData(), palette);
//                it.write(SKIN_PROPERTIES, part.getProperties());
                it.writeBlobs(part.getBlobs());
            });
        }
    });

    public static final ChunkSerializer<Collection<SkinMarker>, Void> SKIN_MARKERS = register(new ChunkSerializer<Collection<SkinMarker>, Void>(ChunkType.MARKER) {

        @Override
        public Collection<SkinMarker> read(ChunkInputStream stream, String name, Void obj) throws IOException {
            int size = stream.readInt();
            ArrayList<SkinMarker> markers = new ArrayList<>();
            for (int i = 0; i < size; ++i) {
                markers.add(new SkinMarker(stream));
            }
            return markers;
        }

        @Override
        public void write(Collection<SkinMarker> value, Void obj, ChunkOutputStream stream) throws IOException {
            stream.writeInt(value.size());
            for (SkinMarker marker : value) {
                marker.writeToStream(stream);
            }
        }
    });


    public static final ChunkSerializer<ChunkCubeData, ChunkPaletteData> SKIN_CUBE_DATA = register(new ChunkSerializer<ChunkCubeData, ChunkPaletteData>(ChunkType.CUBE_DATA) {

        @Override
        public ChunkCubeData read(ChunkInputStream stream, String name, ChunkPaletteData palette) throws IOException {
            ChunkCubeData chunkCubes = new ChunkCubeData(palette);
            chunkCubes.readFromStream(stream);
            return chunkCubes;
        }

        @Override
        public void write(ChunkCubeData chunkCubes, ChunkPaletteData palette, ChunkOutputStream stream) throws IOException {
            stream.writeVariable(chunkCubes);
        }
    });

    public static final ChunkSerializer<SkinPaintData, ChunkPaletteData> SKIN_PAINT_DATA = register(new ChunkSerializer<SkinPaintData, ChunkPaletteData>(ChunkType.PAINT_DATA) {

        @Override
        public SkinPaintData read(ChunkInputStream stream, String name, ChunkPaletteData palette) throws IOException {
            ChunkPaintData chunkPaintData = new ChunkPaintData(palette);
            return chunkPaintData.readFromStream(stream);
        }

        @Override
        public void write(SkinPaintData value, ChunkPaletteData palette, ChunkOutputStream stream) throws IOException {
            ChunkPaintData chunkPaintData = new ChunkPaintData(palette);
            chunkPaintData.writeToStream(value, stream);
        }
    });

    public static final ChunkSerializer<SkinPreviewData, ChunkCubeData> SKIN_PREVIEW_DATA = register(new ChunkSerializer<SkinPreviewData, ChunkCubeData>(ChunkType.PREVIEW_DATA) {

        @Override
        public SkinPreviewData read(ChunkInputStream stream, String name, ChunkCubeData chunkCubes) throws IOException {
            ChunkPreviewData chunkPreviewData = new ChunkPreviewData(chunkCubes);
            return chunkPreviewData.readFromStream(stream);
        }

        @Override
        public void write(SkinPreviewData previewData, ChunkCubeData chunkCubes, ChunkOutputStream stream) throws IOException {
            ChunkPreviewData chunkPreviewData = new ChunkPreviewData(chunkCubes);
            chunkPreviewData.writeToStream(previewData, stream);
        }
    });

    public static final ChunkSerializer<ChunkPaletteData, Void> SKIN_TEXTURE_DATA = register(new ChunkSerializer<ChunkPaletteData, Void>(ChunkType.PALETTE) {

        @Override
        public ChunkPaletteData read(ChunkInputStream stream, String name, Void obj) throws IOException {
            ChunkPaletteData palette = new ChunkPaletteData();
            palette.readFromStream(stream);
            return palette;
        }

        @Override
        public void write(ChunkPaletteData value, Void obj, ChunkOutputStream stream) throws IOException {
            stream.writeVariable(value);
        }
    });

    public static final ChunkSerializer<SkinProperties, Void> SKIN_PROPERTIES = register(new ChunkSerializer<SkinProperties, Void>(ChunkType.PROPERTIES) {

        @Override
        public SkinProperties read(ChunkInputStream stream, String name, Void obj) throws IOException {
            SkinProperties properties = new SkinProperties();
            properties.readFromStream(stream);
            return properties;
        }

        @Override
        public void write(SkinProperties value, Void obj, ChunkOutputStream stream) throws IOException {
            value.writeToStream(stream);
        }

        @Override
        public boolean canWrite(SkinProperties value, Void obj, ChunkOutputStream stream) {
            return !value.isEmpty();
        }
    });

    public static final ChunkSerializer<SkinSettings, Void> SKIN_SETTINGS = register(new ChunkSerializer<SkinSettings, Void>(ChunkType.SKIN_SETTINGS) {

        @Override
        public SkinSettings read(ChunkInputStream stream, String name, Void obj) throws IOException {
            SkinSettings settings = new SkinSettings();
            if (name.equals("SET2")) {
                settings.readFromLegacyStream(stream);
            } else {
                settings.readFromStream(stream);
            }
            return settings;
        }

        @Override
        public boolean canRead(String name) {
            return super.canRead(name) || name.equals("SET2");
        }

        @Override
        public void write(SkinSettings value, Void obj, ChunkOutputStream stream) throws IOException {
            value.writeToStream(stream);
        }

        @Override
        public boolean canWrite(SkinSettings value, Void obj, ChunkOutputStream stream) {
            return !value.isEmpty();
        }
    });

    public static void writeToStream(Skin skin, IOutputStream stream, ChunkContext context) throws IOException {
        ChunkOutputStream stream1 = new ChunkOutputStream(context);
        SKIN.write(skin, null, stream1);
        stream1.transferTo(stream.getOutputStream());
    }

    public static Skin readFromStream(IInputStream stream, ChunkContext context) throws IOException {
        ChunkInputStream stream1 = new ChunkInputStream(stream.getInputStream(), context, null);
        return SKIN.read(stream1, "", null);
    }

    public static Pair<ISkinType, ISkinProperties> readInfoFromStream(IInputStream stream, ChunkContext context) throws IOException {
        ArrayList<String> allows = Lists.newArrayList(ChunkType.PROPERTIES.getName());
        ChunkInputStream stream1 = new ChunkInputStream(stream.getInputStream(), context, allows::contains);
        return SKIN_INFO.read(stream1, "", null);
    }

    private static <T, C> ChunkSerializer<T, C> register(ChunkSerializer<T, C> serializer) {
        return serializer;
    }
}
