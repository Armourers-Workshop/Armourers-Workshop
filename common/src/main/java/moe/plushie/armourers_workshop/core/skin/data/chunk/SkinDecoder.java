package moe.plushie.armourers_workshop.core.skin.data.chunk;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

/**
 * file format:           | header(4b) | version(4b) | color index bits(1b: 1-4) | reserved(15b) | skin type |[ chunk ]
 * chunk format:          | length(4b) | name(4b) | flag(1b) |[ chunk data(from length) ]|
 * skin properties :	  | length | PPTS | flag |[ key/value ]|
 * color palette:		  | length | PALT | flag |[ TTRRGGBB ]|
 * texture paint data:	  | length | TPDT | flag | version(1b) |[ x(1b)/y(1b)/w(1b)/h(1b) |[ color index ]|]|
 * skin part:			  | length | SKPR | flag | id(2b) | skin part type |[ chunk ]|
 * skin part cubes:		  | length | PRCB | flag |[ id(1b) | x(1b)/y(1b)/z(1b) | count(1b) | [ face(1b: 1|2|4|8|16|32) | color index ](1-6)|]|
 * skin part markers:  	  | length | PRMK | flag |[          x(1b)/y(1b)/z(1b) | meta(1b) ]|
 * skin part transform:	  | length | PRTF | flag | scale/rotation/offset |
 * skin part name 		  | length | PRNM | flag | part name |
 * skin part parent:	  | length | PRPP | flag | parent part id(2b) |
 * chunk flag:            1 encrypt, 2 gzip, 3 encrypt+gzip
 */
@SuppressWarnings("unused")
public class SkinDecoder {

    public static final IChunkSerializer<Skin> SKIN = register(ChunkType.SKIN, new IChunkSerializer<Skin>() {

        @Override
        public Skin decode(IChunkDataReader reader) {
            Builder builder = new Builder();
            ISkinType skinType = SkinTypes.byName(reader.readKey().toString());
            reader.read(s -> {
                SkinPart part;
                Object colorPalette = s.read(ChunkType.PALETTE);
                builder.properties = s.read(ChunkType.PROPERTIES);
                builder.paintData = s.read(ChunkType.PAINT_DATA);
                while ((part = s.read(ChunkType.SKIN_PART)) != null) {
                    builder.skinParts.add(part);
                }
            });
            return SkinSerializer.makeSkin(skinType, builder.properties, builder.paintData, builder.skinParts);
        }

        @Override
        public void encode(IChunkDataWriter writer, Skin skin) {
            writer.writeKey(skin.getType().getRegistryName());
            writer.write(null, q -> {
                q.write(ChunkType.PALETTE, null);
                q.write(ChunkType.PROPERTIES, skin.getProperties());
                q.write(ChunkType.PAINT_DATA, skin.getPaintData());
                for (SkinPart part : skin.getParts()) {
                    q.write(ChunkType.SKIN_PART, part);
                }
            });
        }

        class Builder {
            SkinProperties properties;
            SkinPaintData paintData;
            final ArrayList<SkinPart> skinParts = new ArrayList<>();
        }
    });

    public static final IChunkSerializer<SkinPart> SKIN_PART = register(ChunkType.SKIN_PART, new IChunkSerializer<SkinPart>() {
        @Override
        public SkinPart decode(IChunkDataReader reader) {
            int id = reader.readShort();
            ISkinPartType partType = SkinPartTypes.byName(reader.readKey().toString());
            Builder builder = new Builder();
            reader.read(s -> {
                builder.cubes = s.read(ChunkType.CUBE);
                builder.transform = s.read(ChunkType.TRANSFORM);
                builder.markers = s.read(ChunkType.MARKER);
                builder.properties = s.read(ChunkType.PROPERTIES);
                builder.paintData = s.read(ChunkType.PAINT_DATA);
                builder.partName = s.read(ChunkType.SKIN_PART_NAME);
                builder.parent = s.read(ChunkType.SKIN_PART_PARENT);
            });
            return new SkinPart(partType, builder.markers, builder.cubes);
        }

        @Override
        public void encode(IChunkDataWriter writer, SkinPart part) {
            writer.writeShort(part.getId());
            writer.writeKey(part.getType().getRegistryName());
            writer.write(null, q -> {
                q.write(ChunkType.CUBE, part.getCubeData());
                q.write(ChunkType.TRANSFORM, part.getTransform());
                q.write(ChunkType.MARKER, part.getMarkers());
                q.write(ChunkType.PROPERTIES, part.getProperties());
                q.write(ChunkType.PAINT_DATA, part.getPaintData());
                q.write(ChunkType.SKIN_PART_NAME, part.getName());
                q.write(ChunkType.SKIN_PART_PARENT, part.getParent());
            });
        }

        class Builder {
            SkinCubeData cubes;
            Object transform;
            ArrayList<SkinMarker> markers;
            SkinProperties properties;
            SkinPaintData paintData;
            String partName;
            int parent;
        }
    });

    public static final IChunkSerializer<String> SKIN_PART_NAME = register(ChunkType.SKIN_PART_NAME, new IChunkSerializer<String>() {
        @Override
        public String decode(IChunkDataReader reader) {
            return reader.readUTF();
        }

        @Override
        public void encode(IChunkDataWriter writer, String value) {
            writer.writeUTF(value);
        }
    });
    public static final IChunkSerializer<Integer> SKIN_PART_PARENT = register(ChunkType.SKIN_PART_PARENT, new IChunkSerializer<Integer>() {
        @Override
        public Integer decode(IChunkDataReader reader) {
            return reader.readShort();
        }

        @Override
        public void encode(IChunkDataWriter writer, Integer value) {
            writer.writeShort(value);
        }
    });

    public static final IChunkSerializer<SkinCubeData> SKIN_CUBE = register(ChunkType.CUBE, new IChunkSerializer<SkinCubeData>() {
        @Override
        public SkinCubeData decode(IChunkDataReader reader) {
            while (reader.readable()) {
                int id = reader.readByte();
                int x = reader.readByte();
                int y = reader.readByte();
                int z = reader.readByte();
                int flags = 0;
                for (int i = 0; (flags & 0x5f) != 0x5f && i < 6; ++i) {
                    int face = reader.readByte();
                    IPaintColor color = reader.readColor();
                    flags |= face;
                }
            }
            return null;
        }

        @Override
        public void encode(IChunkDataWriter writer, SkinCubeData value) {
            for (int i = 0; i < value.getCubeCount(); ++i) {
                SkinCube cube = null;
                writer.writeByte(cube.getId());
                writer.writeByte(0); // x
                writer.writeByte(0); // y
                writer.writeByte(0); // z
                // merge + resort
                ArrayList<Pair<PaintColor, Integer>> colors = new ArrayList<>();
                for (Pair<PaintColor, Integer> c : colors) {
                    writer.writeByte(c.getValue());
                    writer.writeColor(c.getKey());
                }
            }
        }
    });

    public static final IChunkSerializer<ArrayList<SkinMarker>> SKIN_MARKERS = register(ChunkType.MARKER, new IChunkSerializer<ArrayList<SkinMarker>>() {
        @Override
        public ArrayList<SkinMarker> decode(IChunkDataReader reader) {
            ArrayList<SkinMarker> markers = new ArrayList<>();
            while (reader.readable()) {
                int x = reader.readByte();
                int y = reader.readByte();
                int z = reader.readByte();
                int m = reader.readByte();
                markers.add(new SkinMarker((byte) x, (byte) y, (byte) z, (byte) m));
            }
            return markers;
        }

        @Override
        public void encode(IChunkDataWriter writer, ArrayList<SkinMarker> value) {
            for (SkinMarker marker : value) {
                writer.writeByte(marker.x);
                writer.writeByte(marker.y);
                writer.writeByte(marker.z);
                writer.writeByte(marker.meta);
            }
        }
    });

    public static final IChunkSerializer<SkinPaintData> SKIN_PAINT_DATA = register(ChunkType.PAINT_DATA, new IChunkSerializer<SkinPaintData>() {
        @Override
        public SkinPaintData decode(IChunkDataReader reader) {
            int version = reader.readByte();
            SkinPaintData paintData;
            if (version == 1) {
                paintData = SkinPaintData.v1();
            } else if (version == 2) {
                paintData = SkinPaintData.v2();
            } else {
                return null;
            }
            while (reader.readable()) {
                int x = reader.readByte();
                int y = reader.readByte();
                int width = reader.readByte();
                int height = reader.readByte();
                for (int i = 0; i < height; ++i) {
                    for (int j = 0; j < width; ++j) {
                        IPaintColor color = reader.readColor();
                        paintData.setColor(x + j, y + i, color.getRawValue());
                    }
                }
            }
            return paintData;
        }

        @Override
        public void encode(IChunkDataWriter writer, SkinPaintData value) {

        }
    });

    public static final IChunkSerializer<SkinProperties> SKIN_PROPERTIES = register(ChunkType.PROPERTIES, new IChunkSerializer<SkinProperties>() {
        @Override
        public SkinProperties decode(IChunkDataReader reader) {
            return null;
        }

        @Override
        public void encode(IChunkDataWriter writer, SkinProperties value) {

        }
    });

    public static final IChunkSerializer<Object> SKIN_TRANSFORM = register(ChunkType.TRANSFORM, new IChunkSerializer<Object>() {
        @Override
        public Object decode(IChunkDataReader reader) {
            return null;
        }

        @Override
        public void encode(IChunkDataWriter writer, Object value) {

        }
    });

    public static final IChunkSerializer<Object> SKIN_COLOR_PALETTE = register(ChunkType.PALETTE, new IChunkSerializer<Object>() {
        @Override
        public Object decode(IChunkDataReader reader) {
            return null;
        }

        @Override
        public void encode(IChunkDataWriter writer, Object value) {

        }
    });


    private static <T> IChunkSerializer<T> register(ChunkType type, IChunkSerializer<T> serializer) {
        return serializer;
    }

    public enum ChunkType implements IChunkType {
        SKIN, PAINT_DATA, PROPERTIES, CUBE, MARKER, TRANSFORM, PALETTE, SKIN_PART, SKIN_PART_NAME, SKIN_PART_PARENT
    }
}

