package moe.plushie.armourers_workshop.core.skin.data.serialize.v12;

import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.bake.SkinTexture;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.utils.SkinLog;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public final class SkinSerializerV12 {

    private static final int FILE_VERSION = 13;

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

    private SkinSerializerV12() {
    }

    public static String getTypeNameByLegacyId(int legacyId) {
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

    public static void writeToStream(Skin skin, DataOutputStream stream) throws IOException {
        // Write the skin file version.
        stream.writeInt(FILE_VERSION);
        // Write skin header.
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_HEADER);
        // Write skin props.
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PROPS_HEADER);
        skin.getProperties().writeToStream(stream);
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PROPS_FOOTER);
        // Write the skin type.
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_TYPE_HEADER);
        stream.writeUTF(skin.getType().getRegistryName().toString());
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_TYPE_FOOTER);
        // Write paint data.
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PAINT_HEADER);
        if (skin.hasPaintData()) {
            stream.writeBoolean(true);
            for (int i = 0; i < SkinTexture.TEXTURE_OLD_SIZE; i++) {
                stream.writeInt(skin.getPaintData()[i]);
            }
        } else {
            stream.writeBoolean(false);
        }
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PAINT_FOOTER);
        //Write parts
        stream.writeByte(skin.getParts().size());
        for (SkinPart skinPart : skin.getParts()) {
            StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PART_HEADER);
            SkinPartSerializerV12.saveSkinPart(skinPart, stream);
            StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PART_FOOTER);
        }
        // Write skin footer.
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_FOOTER);
    }

    public static Skin readSkinFromStream(DataInputStream stream, int fileVersion) throws IOException, InvalidCubeTypeException {
        if (fileVersion > 12) {
            String header = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            if (!header.equals(TAG_SKIN_HEADER)) {
                SkinLog.error("Error loading skin header.");
            }

            String propsHeader = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            if (!propsHeader.equals(TAG_SKIN_PROPS_HEADER)) {
                SkinLog.error("Error loading skin props header.");
            }
        }

        SkinProperties properties = new SkinProperties();
        boolean loadedProps = true;
        IOException e = null;
        if (fileVersion < 12) {
            String authorName = stream.readUTF();
            String customName = stream.readUTF();
            String tags = "";
            if (!(fileVersion < 4)) {
                tags = stream.readUTF();
            }
            properties.put(SkinProperty.ALL_AUTHOR_NAME, authorName);
            properties.put(SkinProperty.ALL_CUSTOM_NAME, customName);
            if (!tags.equalsIgnoreCase("")) {
                properties.put(SkinProperty.ALL_KEY_TAGS, tags);
            }
        } else {
            try {
                properties.readFromStream(stream, fileVersion);
            } catch (IOException propE) {
                SkinLog.error("prop load failed");
                e = propE;
                loadedProps = false;
            }
        }

        if (fileVersion > 12) {
            String propsFooter = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            if (!propsFooter.equals(TAG_SKIN_PROPS_FOOTER)) {
                SkinLog.error("Error loading skin props footer.");
            }

            String typeHeader = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            if (!typeHeader.equals(TAG_SKIN_TYPE_HEADER)) {
                SkinLog.error("Error loading skin type header.");
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
                String regName = stream.readUTF();
                skinType = SkinTypes.byName(regName);
            } else {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
                    if (sb.toString().endsWith("armourers:")) {
                        break;
                    }
                }
                SkinLog.info("Got armourers");
                sb = new StringBuilder();
                sb.append("armourers:");
                while (SkinTypes.byName(sb.toString()) == null) {
                    sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
                }
                SkinLog.info(sb.toString());
                skinType = SkinTypes.byName(sb.toString());
                SkinLog.info("got failed type " + skinType);
            }
        }

        if (fileVersion > 12) {
            String typeFooter = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            if (!typeFooter.equals(TAG_SKIN_TYPE_FOOTER)) {
                SkinLog.error("Error loading skin type footer.");
            }
        }

        if (skinType == null) {
            throw new InvalidCubeTypeException();
        }

        if (fileVersion > 12) {
            String typeFooter = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            if (!typeFooter.equals(TAG_SKIN_PAINT_HEADER)) {
                SkinLog.error("Error loading skin paint header.");
            }
        }
        int[] paintData = null;
        if (fileVersion > 7) {
            boolean hasPaintData = stream.readBoolean();
            if (hasPaintData) {
                paintData = new int[SkinTexture.TEXTURE_OLD_SIZE];
                for (int i = 0; i < SkinTexture.TEXTURE_OLD_SIZE; i++) {
                    paintData[i] = stream.readInt();
                }
            }
        }
        if (fileVersion > 12) {
            String typeFooter = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            if (!typeFooter.equals(TAG_SKIN_PAINT_FOOTER)) {
                SkinLog.error("Error loading skin paint footer.");
            }
        }

        int size = stream.readByte();
        ArrayList<SkinPart> parts = new ArrayList<SkinPart>();
        for (int i = 0; i < size; i++) {
            if (fileVersion > 12) {
                String partHeader = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
                if (!partHeader.equals(TAG_SKIN_PART_HEADER)) {
                    SkinLog.error("Error loading skin part header.");
                }
            }
            SkinPart part = SkinPartSerializerV12.loadSkinPart(stream, fileVersion);
            if (fileVersion > 12) {
                String partFooter = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
                if (!partFooter.equals(TAG_SKIN_PART_FOOTER)) {
                    SkinLog.error("Error loading skin part footer.");
                }
            }
            parts.add(part);
        }

        if (fileVersion > 12) {
            String footer = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            if (!footer.equals(TAG_SKIN_FOOTER)) {
                SkinLog.error("Error loading skin footer.");
            }
        }
        return SkinSerializer.makeSkin(skinType, properties, paintData, parts);
    }

    public static ISkinType readSkinTypeNameFromStream(DataInputStream stream, int fileVersion) throws IOException {
        if (fileVersion > 12) {
            String header = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            if (!header.equals(TAG_SKIN_HEADER)) {
                SkinLog.error("Error loading skin header.");
            }

            String propsHeader = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            if (!propsHeader.equals(TAG_SKIN_PROPS_HEADER)) {
                SkinLog.error("Error loading skin props header.");
            }
        }

        SkinProperties properties = new SkinProperties();
        boolean loadedProps = true;
        IOException e = null;
        if (fileVersion < 12) {
            String authorName = stream.readUTF();
            String customName = stream.readUTF();
            String tags = "";
            if (!(fileVersion < 4)) {
                tags = stream.readUTF();
            } else {
                tags = "";
            }
            properties.put(SkinProperty.ALL_AUTHOR_NAME, authorName);
            properties.put(SkinProperty.ALL_CUSTOM_NAME, customName);
            if (!tags.equalsIgnoreCase("")) {
                properties.put(SkinProperty.ALL_KEY_TAGS, tags);
            }
        } else {
            try {
                properties.readFromStream(stream, fileVersion);
            } catch (IOException propE) {
                SkinLog.error("prop load failed");
                e = propE;
                loadedProps = false;
            }
        }

        if (fileVersion > 12) {
            String propsFooter = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            if (!propsFooter.equals(TAG_SKIN_PROPS_FOOTER)) {
                SkinLog.error("Error loading skin props footer.");
            }

            String typeHeader = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            if (!typeHeader.equals(TAG_SKIN_TYPE_HEADER)) {
                SkinLog.error("Error loading skin type header.");
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
                String regName = stream.readUTF();
                equipmentSkinType = SkinTypes.byName(regName);
            } else {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
                    if (sb.toString().endsWith("armourers:")) {
                        break;
                    }
                }
                SkinLog.info("Got armourers");
                sb = new StringBuilder();
                sb.append("armourers:");
                while (SkinTypes.byName(sb.toString()) == null) {
                    sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
                }
                SkinLog.info(sb.toString());
                equipmentSkinType = SkinTypes.byName(sb.toString());
                SkinLog.info("got failed type " + equipmentSkinType);
            }
        }
        return equipmentSkinType;
    }
}
