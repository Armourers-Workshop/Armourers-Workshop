package moe.plushie.armourers_workshop.common.skin.data.serialize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.common.exception.NewerFileVersionException;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.serialize.v12.SkinSerializerV12;
import moe.plushie.armourers_workshop.common.skin.data.serialize.v13.SkinSerializerV13;
import moe.plushie.armourers_workshop.common.skin.data.serialize.v14.SkinSerializerV14;

public class SkinSerializer {

    public static final int MAX_FILE_VERSION = 13;

    public static void writeToStream(Skin skin, DataOutputStream stream) throws IOException {
        // Temporary code to save as newer file version for advanced skins.
        int saveVersion = MAX_FILE_VERSION;
        for (SkinPart skinPart : skin.getParts()) {
            if (skinPart.getPartType().getPartName().equals("advanced_part")) {
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
        // case 14:
        // return SkinSerializerV14.readSkinTypeNameFromStream(stream, fileVersion);
        case 13:
            return SkinSerializerV13.readSkinTypeNameFromStream(stream, fileVersion);
        default:
            return SkinSerializerV12.readSkinTypeNameFromStream(stream, fileVersion);
        }
    }
}
