package moe.plushie.armourers_workshop.core.skin.serializer.v12;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.ISkinSerializer;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.texture.PlayerTextureModel;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public final class SkinSerializerV12 implements ISkinSerializer {

    public static final int FILE_VERSION = 12;

    private static final String TAG_SKIN_HEADER = "AW-SKIN-START";

    private static final String TAG_SKIN_PROPS_HEADER = "PROPS-START";
    private static final String TAG_SKIN_PROPS_FOOTER = "PROPS-END";

    private static final String TAG_SKIN_TYPE_HEADER = "TYPE-START";
    private static final String TAG_SKIN_TYPE_FOOTER = "TYPE-END";

    private static final String TAG_SKIN_PAINT_HEADER = "PAINT-START";
    private static final String TAG_SKIN_PAINT_FOOTER = "PAINT-END";

    private static final String TAG_SKIN_PART_HEADER = "PART-START";
    private static final String TAG_SKIN_PART_FOOTER = "PART-END";

    private static final String TAG_SKIN_FOOTER = "AW-SKIN-END";

    private final SkinPartSerializerV12 partSerializer = new SkinPartSerializerV12();

    public SkinSerializerV12() {
    }

    public String getTypeNameByLegacyId(int legacyId) {
        switch (legacyId) {
            case 0:
                return "armourers:head";
            case 1:
                return "armourers:chest";
            case 2:
                return "armourers:legs";
            case 3:
                return "armourers:skirt";
            case 4:
                return "armourers:feet";
            case 5:
                return "armourers:sword";
            case 6:
                return "armourers:bow";
            case 7:
                return "armourers:arrow";
            default:
                return null;
        }
    }

    @Override
    public void writeToStream(Skin skin, IOutputStream stream, int fileVersion) throws IOException {
        // Write skin header.
        stream.writeString(TAG_SKIN_HEADER);
        // Write skin props.
        stream.writeString(TAG_SKIN_PROPS_HEADER);
        stream.writeSkinProperties(skin.getProperties());
        stream.writeString(TAG_SKIN_PROPS_FOOTER);
        // Write the skin type.
        stream.writeString(TAG_SKIN_TYPE_HEADER);
        stream.writeType(skin.getType());
        stream.writeString(TAG_SKIN_TYPE_FOOTER);
        // Write paint data.
        stream.writeString(TAG_SKIN_PAINT_HEADER);
        if (skin.getPaintData() != null) {
            stream.writeBoolean(true);
            // TODO: Support v2 skin
            int[] colors = skin.getPaintData().getData();
            for (int i = 0; i < PlayerTextureModel.TEXTURE_OLD_SIZE; i++) {
                stream.writeInt(colors[i]);
            }
        } else {
            stream.writeBoolean(false);
        }
        stream.writeString(TAG_SKIN_PAINT_FOOTER);
        //Write parts
        stream.writeByte(skin.getParts().size());
        for (SkinPart skinPart : skin.getParts()) {
            stream.writeString(TAG_SKIN_PART_HEADER);
            partSerializer.saveSkinPart(skinPart, stream);
            stream.writeString(TAG_SKIN_PART_FOOTER);
        }
        // Write skin footer.
        stream.writeString(TAG_SKIN_FOOTER);
    }

    @Override
    public Skin readFromStream(IInputStream stream, int fileVersion) throws IOException, InvalidCubeTypeException {
        if (fileVersion > 12) {
            String header = stream.readString();
            if (!header.equals(TAG_SKIN_HEADER)) {
                ModLog.error("Error loading skin header.");
            }

            String propsHeader = stream.readString();
            if (!propsHeader.equals(TAG_SKIN_PROPS_HEADER)) {
                ModLog.error("Error loading skin props header.");
            }
        }

        SkinProperties properties = null;
        boolean loadedProps = true;
        IOException e = null;
        if (fileVersion < 12) {
            String authorName = stream.readString();
            String customName = stream.readString();
            String tags = "";
            if (!(fileVersion < 4)) {
                tags = stream.readString();
            }
            properties = new SkinProperties();
            properties.put(SkinProperty.ALL_AUTHOR_NAME, authorName);
            properties.put(SkinProperty.ALL_CUSTOM_NAME, customName);
            if (!tags.equalsIgnoreCase("")) {
                properties.put(SkinProperty.ALL_KEY_TAGS, tags);
            }
        } else {
            try {
                properties = stream.readSkinProperties();
            } catch (IOException propE) {
                ModLog.error("prop load failed");
                e = propE;
                loadedProps = false;
                properties = new SkinProperties();
            }
        }

        if (fileVersion > 12) {
            String propsFooter = stream.readString();
            if (!propsFooter.equals(TAG_SKIN_PROPS_FOOTER)) {
                ModLog.error("Error loading skin props footer.");
            }

            String typeHeader = stream.readString();
            if (!typeHeader.equals(TAG_SKIN_TYPE_HEADER)) {
                ModLog.error("Error loading skin type header.");
            }
        }

        ISkinType skinType = null;

        if (fileVersion < 5) {
            if (loadedProps) {
                String regName = getTypeNameByLegacyId(stream.readByte() - 1);
                skinType = SkinTypes.byName(regName);
            } else {
                throw e;
            }
        } else {
            if (loadedProps) {
                skinType = stream.readType(SkinTypes::byName);
            } else {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
                    if (sb.toString().endsWith("armourers:")) {
                        break;
                    }
                }
                ModLog.info("Got armourers");
                sb = new StringBuilder();
                sb.append("armourers:");
                while (SkinTypes.byName(sb.toString()) == null) {
                    sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
                }
                ModLog.info(sb.toString());
                skinType = SkinTypes.byName(sb.toString());
                ModLog.info("got failed type " + skinType);
            }
        }

        if (fileVersion > 12) {
            String typeFooter = stream.readString();
            if (!typeFooter.equals(TAG_SKIN_TYPE_FOOTER)) {
                ModLog.error("Error loading skin type footer.");
            }
        }

        if (skinType == null) {
            throw new InvalidCubeTypeException();
        }

        if (fileVersion > 12) {
            String typeFooter = stream.readString();
            if (!typeFooter.equals(TAG_SKIN_PAINT_HEADER)) {
                ModLog.error("Error loading skin paint header.");
            }
        }

        // TODO: support v2 texture
        SkinPaintData paintData = null;
        if (fileVersion > 7) {
            boolean hasPaintData = stream.readBoolean();
            if (hasPaintData) {
                paintData = SkinPaintData.v1();
                int[] colors = paintData.getData();
                for (int i = 0; i < PlayerTextureModel.TEXTURE_OLD_SIZE; i++) {
                    colors[i] = stream.readInt();
                }
            }
        }
        if (fileVersion > 12) {
            String typeFooter = stream.readString();
            if (!typeFooter.equals(TAG_SKIN_PAINT_FOOTER)) {
                ModLog.error("Error loading skin paint footer.");
            }
        }

        int size = stream.readByte();
        ArrayList<SkinPart> parts = new ArrayList<SkinPart>();
        for (int i = 0; i < size; i++) {
            if (fileVersion > 12) {
                String partHeader = stream.readString();
                if (!partHeader.equals(TAG_SKIN_PART_HEADER)) {
                    ModLog.error("Error loading skin part header.");
                }
            }
            SkinPart part = partSerializer.loadSkinPart(stream, fileVersion);
            if (fileVersion > 12) {
                String partFooter = stream.readString();
                if (!partFooter.equals(TAG_SKIN_PART_FOOTER)) {
                    ModLog.error("Error loading skin part footer.");
                }
            }
            parts.add(part);
        }

        if (fileVersion > 12) {
            String footer = stream.readString();
            if (!footer.equals(TAG_SKIN_FOOTER)) {
                ModLog.error("Error loading skin footer.");
            }
        }

        Skin.Builder builder = new Skin.Builder(skinType);
        builder.properties(properties);
        builder.paintData(paintData);
        builder.parts(parts);
        return builder.build();
    }

    @Override
    public SkinFileHeader readInfoFromStream(IInputStream stream, int fileVersion) throws IOException {
        if (fileVersion > 12) {
            String header = stream.readString();
            if (!header.equals(TAG_SKIN_HEADER)) {
                ModLog.error("Error loading skin header.");
            }

            String propsHeader = stream.readString();
            if (!propsHeader.equals(TAG_SKIN_PROPS_HEADER)) {
                ModLog.error("Error loading skin props header.");
            }
        }

        SkinProperties properties = null;
        boolean loadedProps = true;
        IOException e = null;
        if (fileVersion < 12) {
            String authorName = stream.readString();
            String customName = stream.readString();
            String tags = "";
            if (!(fileVersion < 4)) {
                tags = stream.readString();
            } else {
                tags = "";
            }
            properties = new SkinProperties();
            properties.put(SkinProperty.ALL_AUTHOR_NAME, authorName);
            properties.put(SkinProperty.ALL_CUSTOM_NAME, customName);
            if (!tags.equalsIgnoreCase("")) {
                properties.put(SkinProperty.ALL_KEY_TAGS, tags);
            }
        } else {
            try {
                properties = stream.readSkinProperties();
            } catch (IOException propE) {
                ModLog.error("prop load failed");
                e = propE;
                loadedProps = false;
                properties = new SkinProperties();
            }
        }

        if (fileVersion > 12) {
            String propsFooter = stream.readString();
            if (!propsFooter.equals(TAG_SKIN_PROPS_FOOTER)) {
                ModLog.error("Error loading skin props footer.");
            }

            String typeHeader = stream.readString();
            if (!typeHeader.equals(TAG_SKIN_TYPE_HEADER)) {
                ModLog.error("Error loading skin type header.");
            }
        }

        ISkinType equipmentSkinType = null;

        if (fileVersion < 5) {
            if (loadedProps) {
                String regName = getTypeNameByLegacyId(stream.readByte() - 1);
                equipmentSkinType = SkinTypes.byName(regName);
            } else {
                throw e;
            }
        } else {
            if (loadedProps) {
                equipmentSkinType = stream.readType(SkinTypes::byName);
            } else {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
                    if (sb.toString().endsWith("armourers:")) {
                        break;
                    }
                }
                ModLog.info("Got armourers");
                sb = new StringBuilder();
                sb.append("armourers:");
                while (SkinTypes.byName(sb.toString()) == null) {
                    sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
                }
                ModLog.info(sb.toString());
                equipmentSkinType = SkinTypes.byName(sb.toString());
                ModLog.info("got failed type " + equipmentSkinType);
            }
        }
        return SkinFileHeader.optimized(fileVersion, equipmentSkinType, properties);
    }

    @Override
    public int getSupportedVersion() {
        return FILE_VERSION;
    }

    @Override
    public boolean isSupportedVersion(int fileVersion) {
        return fileVersion <= FILE_VERSION;
    }
}
