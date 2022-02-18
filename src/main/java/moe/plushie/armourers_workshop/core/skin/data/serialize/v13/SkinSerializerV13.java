package moe.plushie.armourers_workshop.core.skin.data.serialize.v13;

import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.render.bake.PlayerTexture;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.utils.AWLog;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public final class SkinSerializerV13 {

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

    private static final String KEY_TAGS = "tags";

    private SkinSerializerV13() {
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
            for (int i = 0; i < PlayerTexture.TEXTURE_OLD_SIZE; i++) {
                stream.writeInt(skin.getPaintData()[i]);
            }
        } else {
            stream.writeBoolean(false);
        }
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PAINT_FOOTER);
        // Write parts
        stream.writeByte(skin.getParts().size());
        for (SkinPart skinPart : skin.getParts()) {
            StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PART_HEADER);
            SkinPartSerializerV13.saveSkinPart(skinPart, stream);
            StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PART_FOOTER);
        }
        // Write skin footer.
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_FOOTER);
    }

    public static Skin readSkinFromStream(DataInputStream stream, int fileVersion) throws IOException, InvalidCubeTypeException {
        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_HEADER)) {
            AWLog.error("Error loading skin header.");
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PROPS_HEADER)) {
            AWLog.error("Error loading skin props header.");
        }

        SkinProperties properties = new SkinProperties();
        boolean loadedProps = true;
        IOException e = null;
        try {
            properties.readFromStream(stream, fileVersion);
        } catch (IOException propE) {
            AWLog.error("prop load failed");
            e = propE;
            loadedProps = false;
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PROPS_FOOTER)) {
            AWLog.error("Error loading skin props footer.");
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_TYPE_HEADER)) {
            AWLog.error("Error loading skin type header.");
        }

        ISkinType skinType = null;

        if (loadedProps) {
            String regName = stream.readUTF();
            skinType = SkinTypes.byName(regName);
        } else {
            StringBuilder sb = new StringBuilder();
            while (true) {
                sb.append(new String(new byte[]{stream.readByte()}, "UTF-8"));
                if (sb.toString().endsWith("armourers:")) {
                    break;
                }
            }
            AWLog.info("Got armourers");
            sb = new StringBuilder();
            sb.append("armourers:");
            while (SkinTypes.byName(sb.toString()) == null) {
                sb.append(new String(new byte[]{stream.readByte()}, "UTF-8"));
            }
            AWLog.info(sb.toString());
            skinType = SkinTypes.byName(sb.toString());
            AWLog.info("got failed type " + skinType);
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_TYPE_FOOTER)) {
            AWLog.error("Error loading skin type footer.");
        }

        if (skinType == null) {
            throw new InvalidCubeTypeException();
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PAINT_HEADER)) {
            AWLog.error("Error loading skin paint header.");
        }

        int[] paintData = null;
        boolean hasPaintData = stream.readBoolean();
        if (hasPaintData) {
            paintData = new int[PlayerTexture.TEXTURE_OLD_SIZE];
            for (int i = 0; i < PlayerTexture.TEXTURE_OLD_SIZE; i++) {
                paintData[i] = stream.readInt();
            }
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PAINT_FOOTER)) {
            AWLog.error("Error loading skin paint footer.");
        }

        int size = stream.readByte();
        ArrayList<SkinPart> parts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PART_HEADER)) {
                AWLog.error("Error loading skin part header.");
            }
            SkinPart part = SkinPartSerializerV13.loadSkinPart(stream, fileVersion);
            parts.add(part);
            if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PART_FOOTER)) {
                AWLog.error("Error loading skin part footer.");
            }
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_FOOTER)) {
            AWLog.error("Error loading skin footer.");
        }

        return SkinSerializer.makeSkin(skinType, properties, paintData, parts);
    }

    public static ISkinType readSkinTypeNameFromStream(DataInputStream stream, int fileVersion) throws IOException {
        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_HEADER)) {
            AWLog.error("Error loading skin header.");
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PROPS_HEADER)) {
            AWLog.error("Error loading skin props header.");
        }

        SkinProperties properties = new SkinProperties();
        boolean loadedProps = true;
        IOException e = null;
        try {
            properties.readFromStream(stream, fileVersion);
        } catch (IOException propE) {
            AWLog.error("prop load failed");
            e = propE;
            loadedProps = false;
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PROPS_FOOTER)) {
            AWLog.error("Error loading skin props footer.");
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_TYPE_HEADER)) {
            AWLog.error("Error loading skin type header.");
        }

        ISkinType skinType = null;

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
            AWLog.info("Got armourers");
            sb = new StringBuilder();
            sb.append("armourers:");
            while (SkinTypes.byName(sb.toString()) == null) {
                sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
            }
            AWLog.info(sb.toString());
            skinType = SkinTypes.byName(sb.toString());
            AWLog.info("got failed type " + skinType);
        }
        return skinType;
    }
}
