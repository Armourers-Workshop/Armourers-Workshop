package moe.plushie.armourers_workshop.common.skin.data.serialize.v14;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.common.exception.NewerFileVersionException;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.data.SkinTexture;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.StreamUtils;

public final class SkinSerializerV14 {

    /**
     * V14 Changes
     * 
     * Add protected skin option.
     * Move skin paint to each skin part. (can't have paint without a part)
     * Moved skin type to the start of the file.
     * 
     * 
     * Add skin properties to each skin part, also add more data types.
     */

    private static final int FILE_VERSION = 14;

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

    private SkinSerializerV14() {
    }

    public static void writeToStream(Skin skin, DataOutputStream stream) throws IOException {
        // Write the skin file version.
        stream.writeInt(FILE_VERSION);
        // Write skin header.
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_HEADER);
        // Write the skin type.
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_TYPE_HEADER);
        stream.writeUTF(skin.getSkinType().getRegistryName());
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_TYPE_FOOTER);
        // Write skin props.
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PROPS_HEADER);
        skin.getProperties().writeToStream(stream);
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PROPS_FOOTER);
        // Write paint data.
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PAINT_HEADER);
        if (skin.hasPaintData()) {
            stream.writeBoolean(true);
            for (int i = 0; i < SkinTexture.TEXTURE_SIZE; i++) {
                stream.writeInt(skin.getPaintData()[i]);
            }
        } else {
            stream.writeBoolean(false);
        }
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PAINT_FOOTER);
        // Write parts
        stream.writeByte(skin.getParts().size());
        for (int i = 0; i < skin.getParts().size(); i++) {
            SkinPart skinPart = skin.getParts().get(i);
            StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PART_HEADER);
            SkinPartSerializerV14.saveSkinPart(skinPart, stream);
            StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_PART_FOOTER);
        }
        // Write skin footer.
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, TAG_SKIN_FOOTER);
    }

    public static Skin readSkinFromStream(DataInputStream stream, int fileVersion) throws IOException, NewerFileVersionException, InvalidCubeTypeException {
        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_HEADER)) {
            ModLogger.log(Level.ERROR, "Error loading skin header.");
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_TYPE_HEADER)) {
            ModLogger.log(Level.ERROR, "Error loading skin type header.");
        }

        String regName = stream.readUTF();
        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(regName);

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_TYPE_FOOTER)) {
            ModLogger.log(Level.ERROR, "Error loading skin type footer.");
        }

        if (skinType == null) {
            throw new InvalidCubeTypeException();
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PROPS_HEADER)) {
            ModLogger.log(Level.ERROR, "Error loading skin props header.");
        }
        SkinProperties properties = new SkinProperties();
        IOException e = null;
        try {
            properties.readFromStream(stream, fileVersion);
        } catch (IOException propE) {
            ModLogger.log(Level.ERROR, "prop load failed");
            e = propE;
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PROPS_FOOTER)) {
            ModLogger.log(Level.ERROR, "Error loading skin props footer.");
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PAINT_HEADER)) {
            ModLogger.log(Level.ERROR, "Error loading skin paint header.");
        }

        int[] paintData = null;
        boolean hasPaintData = stream.readBoolean();
        if (hasPaintData) {
            paintData = new int[SkinTexture.TEXTURE_SIZE];
            for (int i = 0; i < SkinTexture.TEXTURE_SIZE; i++) {
                paintData[i] = stream.readInt();
            }
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PAINT_FOOTER)) {
            ModLogger.log(Level.ERROR, "Error loading skin paint footer.");
        }

        int size = stream.readByte();
        ArrayList<SkinPart> parts = new ArrayList<SkinPart>();
        for (int i = 0; i < size; i++) {
            if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PART_HEADER)) {
                ModLogger.log(Level.ERROR, "Error loading skin part header.");
            }
            SkinPart part = SkinPartSerializerV14.loadSkinPart(stream, fileVersion);
            parts.add(part);
            if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_PART_FOOTER)) {
                ModLogger.log(Level.ERROR, "Error loading skin part footer.");
            }
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_FOOTER)) {
            ModLogger.log(Level.ERROR, "Error loading skin footer.");
        }

        // Update skin properties.
        if (SkinProperties.PROP_MODEL_OVERRIDE.getValue(properties)) {
            if (skinType == SkinTypeRegistry.skinHead) {
                SkinProperties.PROP_MODEL_OVERRIDE_HEAD.setValue(properties, true);
            }
            if (skinType == SkinTypeRegistry.skinChest) {
                SkinProperties.PROP_MODEL_OVERRIDE_CHEST.setValue(properties, true);
                SkinProperties.PROP_MODEL_OVERRIDE_ARM_LEFT.setValue(properties, true);
                SkinProperties.PROP_MODEL_OVERRIDE_ARM_RIGHT.setValue(properties, true);
            }
            if (skinType == SkinTypeRegistry.skinLegs) {
                SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT.setValue(properties, true);
                SkinProperties.PROP_MODEL_OVERRIDE_LEG_RIGHT.setValue(properties, true);
            }
            if (skinType == SkinTypeRegistry.skinFeet) {
                SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT.setValue(properties, true);
                SkinProperties.PROP_MODEL_OVERRIDE_LEG_RIGHT.setValue(properties, true);
            }
            if (skinType == SkinTypeRegistry.skinOutfit) {
                // SkinProperties.PROP_MODEL_OVERRIDE_HEAD.setValue(properties, true);
                // SkinProperties.PROP_MODEL_OVERRIDE_CHEST.setValue(properties, true);
                // SkinProperties.PROP_MODEL_OVERRIDE_ARM_LEFT.setValue(properties, true);
                // SkinProperties.PROP_MODEL_OVERRIDE_ARM_RIGHT.setValue(properties, true);
                // SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT.setValue(properties, true);
                // SkinProperties.PROP_MODEL_OVERRIDE_LEG_RIGHT.setValue(properties, true);
            }
            SkinProperties.PROP_MODEL_OVERRIDE.clearValue(properties);
        }
        if (SkinProperties.PROP_MODEL_HIDE_OVERLAY.getValue(properties)) {
            if (skinType == SkinTypeRegistry.skinHead) {
                SkinProperties.PROP_MODEL_HIDE_OVERLAY_HEAD.setValue(properties, true);
            }
            if (skinType == SkinTypeRegistry.skinChest) {
                SkinProperties.PROP_MODEL_HIDE_OVERLAY_CHEST.setValue(properties, true);
                SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_LEFT.setValue(properties, true);
                SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_RIGHT.setValue(properties, true);
            }
            if (skinType == SkinTypeRegistry.skinLegs) {
                SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_LEFT.setValue(properties, true);
                SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_RIGHT.setValue(properties, true);
            }
            if (skinType == SkinTypeRegistry.skinFeet) {
                SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_LEFT.setValue(properties, true);
                SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_RIGHT.setValue(properties, true);
            }
            if (skinType == SkinTypeRegistry.skinOutfit) {
                // SkinProperties.PROP_MODEL_HIDE_OVERLAY_HEAD.setValue(properties, true);
                // SkinProperties.PROP_MODEL_HIDE_OVERLAY_CHEST.setValue(properties, true);
                // SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_LEFT.setValue(properties, true);
                // SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_RIGHT.setValue(properties, true);
                // SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_LEFT.setValue(properties, true);
                // SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_RIGHT.setValue(properties, true);
            }
            SkinProperties.PROP_MODEL_HIDE_OVERLAY.clearValue(properties);
        }

        return new Skin(properties, skinType, paintData, parts);
    }

    public static ISkinType readSkinTypeNameFromStream(DataInputStream stream, int fileVersion) throws IOException, NewerFileVersionException {
        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_HEADER)) {
            ModLogger.log(Level.ERROR, "Error loading skin header.");
        }

        if (!StreamUtils.readString(stream, StandardCharsets.US_ASCII).equals(TAG_SKIN_TYPE_HEADER)) {
            ModLogger.log(Level.ERROR, "Error loading skin type header.");
        }

        String regName = stream.readUTF();
        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(regName);

        return skinType;
    }
}
