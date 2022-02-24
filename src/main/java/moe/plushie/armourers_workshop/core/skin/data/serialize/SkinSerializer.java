package moe.plushie.armourers_workshop.core.skin.data.serialize;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v12.SkinSerializerV12;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v13.SkinSerializerV13;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v14.SkinSerializerV14;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.exception.NewerFileVersionException;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SkinSerializer {

    public static final int MAX_FILE_VERSION = 13;

    public static void writeToStream(Skin skin, DataOutputStream stream) throws IOException {
        // Temporary code to save as newer file version for advanced skins.
        int saveVersion = MAX_FILE_VERSION;
        for (SkinPart skinPart : skin.getParts()) {
            if (skinPart.getType() == SkinPartTypes.ADVANCED) {
                saveVersion = 14;
            }
        }
        writeToStream(skin, stream, MAX_FILE_VERSION);
    }

    public static void writeToStream(Skin skin, DataOutputStream stream, int fileVersion) throws IOException {
        switch (fileVersion) {
            case 14:
                SkinSerializerV14.writeToStream(skin, stream);
                break;
            case 13:
                SkinSerializerV13.writeToStream(skin, stream);
                break;
            default:
                SkinSerializerV12.writeToStream(skin, stream);
                break;
        }
    }

    public static Skin readSkinFromStream(DataInputStream stream) throws IOException, NewerFileVersionException, InvalidCubeTypeException {
        int fileVersion = stream.readInt();
        if (fileVersion > MAX_FILE_VERSION) {
            throw new NewerFileVersionException();
        }
        switch (fileVersion) {
            case 14:
                return SkinSerializerV14.readSkinFromStream(stream, fileVersion);
            case 13:
                return SkinSerializerV13.readSkinFromStream(stream, fileVersion);
            default:
                return SkinSerializerV12.readSkinFromStream(stream, fileVersion);
        }
    }

    public static ISkinType readSkinTypeNameFromStream(DataInputStream stream) throws IOException, NewerFileVersionException {
        int fileVersion = stream.readInt();
        if (fileVersion > MAX_FILE_VERSION) {
            throw new NewerFileVersionException();
        }
        switch (fileVersion) {
//            case 14:
//                 return SkinSerializerV14.readSkinTypeNameFromStream(stream, fileVersion);
            case 13:
                return SkinSerializerV13.readSkinTypeNameFromStream(stream, fileVersion);
            default:
                return SkinSerializerV12.readSkinTypeNameFromStream(stream, fileVersion);
        }
    }

    public static Skin makeSkin(ISkinType skinType, SkinProperties properties, int[] paintData, ArrayList<SkinPart> skinParts) {
        // Update skin properties.
        if (properties.get(SkinProperty.MODEL_OVERRIDE)) {
            if (skinType == SkinTypes.ARMOR_HEAD) {
                properties.put(SkinProperty.MODEL_OVERRIDE_HEAD, true);
            }
            if (skinType == SkinTypes.ARMOR_CHEST) {
                properties.put(SkinProperty.MODEL_OVERRIDE_CHEST, true);
                properties.put(SkinProperty.MODEL_OVERRIDE_ARM_LEFT, true);
                properties.put(SkinProperty.MODEL_OVERRIDE_ARM_RIGHT, true);
            }
            if (skinType == SkinTypes.ARMOR_LEGS) {
                properties.put(SkinProperty.MODEL_OVERRIDE_LEG_LEFT, true);
                properties.put(SkinProperty.MODEL_OVERRIDE_LEG_RIGHT, true);
            }
            if (skinType == SkinTypes.ARMOR_FEET) {
                properties.put(SkinProperty.MODEL_OVERRIDE_LEG_LEFT, true);
                properties.put(SkinProperty.MODEL_OVERRIDE_LEG_RIGHT, true);
            }
            properties.remove(SkinProperty.MODEL_OVERRIDE);
        }
        if (properties.get(SkinProperty.MODEL_HIDE_OVERLAY)) {
            if (skinType == SkinTypes.ARMOR_HEAD) {
                properties.put(SkinProperty.MODEL_HIDE_OVERLAY_HEAD, true);
            }
            if (skinType == SkinTypes.ARMOR_CHEST) {
                properties.put(SkinProperty.MODEL_HIDE_OVERLAY_CHEST, true);
                properties.put(SkinProperty.MODEL_HIDE_OVERLAY_ARM_LEFT, true);
                properties.put(SkinProperty.MODEL_HIDE_OVERLAY_ARM_RIGHT, true);
            }
            if (skinType == SkinTypes.ARMOR_LEGS) {
                properties.put(SkinProperty.MODEL_HIDE_OVERLAY_LEG_LEFT, true);
                properties.put(SkinProperty.MODEL_HIDE_OVERLAY_LEG_RIGHT, true);
            }
            if (skinType == SkinTypes.ARMOR_FEET) {
                properties.put(SkinProperty.MODEL_HIDE_OVERLAY_LEG_LEFT, true);
                properties.put(SkinProperty.MODEL_HIDE_OVERLAY_LEG_RIGHT, true);
            }
            properties.remove(SkinProperty.MODEL_HIDE_OVERLAY);
        }

        // bind properties to part.
        for (SkinPart part : skinParts) {
            part.setProperties(properties);
        }
        String skinIndexs = properties.get(SkinProperty.OUTFIT_PART_INDEXS);
        if (skinIndexs != null && !skinIndexs.equals("")) {
            String[] split = skinIndexs.split(":");
            int partIndex = 0;
            for (int skinIndex = 0; skinIndex < split.length; ++skinIndex) {
                SkinProperties stub = new SkinProperties.Stub(properties, skinIndex);
                int count = Integer.parseInt(split[skinIndex]);
                while (partIndex < count) {
                    if (partIndex < skinParts.size()) {
                        SkinPart skinPart = skinParts.get(partIndex);
                        skinPart.setProperties(stub);
                    }
                    partIndex += 1;
                }
                partIndex = count;
            }
        }
        return new Skin(properties, skinType, paintData, skinParts);
    }


    public static void writeSkinToBuffer(Skin skin, ByteBuf buffer) {
    }
    public static Skin readSkinByBuffer(ByteBuf buffer) {
        return null;
    }

    public static void writeSkinDescriptorToBuffer(SkinDescriptor descriptor, ByteBuf buffer) {
    }
    public static SkinDescriptor readSkinDescriptorByBuffer(ByteBuf buffer) {
        return null;
    }
}
