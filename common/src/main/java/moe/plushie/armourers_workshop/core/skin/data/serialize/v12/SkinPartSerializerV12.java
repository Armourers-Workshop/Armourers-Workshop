package moe.plushie.armourers_workshop.core.skin.data.serialize.v12;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.StreamUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public final class SkinPartSerializerV12 {

    private SkinPartSerializerV12() {
    }

    public static String getTypeNameByLegacyId(int legacyId) {
        switch (legacyId) {
            case 0:
                return "armourers:head.base";
            case 1:
                return "armourers:chest.base";
            case 2:
                return "armourers:chest.leftArm";
            case 3:
                return "armourers:chest.rightArm";
            case 4:
                return "armourers:legs.leftLeg";
            case 5:
                return "armourers:legs.rightLeg";
            case 6:
                return "armourers:skirt.base";
            case 7:
                return "armourers:feet.leftFoot";
            case 8:
                return "armourers:feet.rightFoot";
            case 9:
                return "armourers:sword.base";
            case 10:
                return "armourers:bow.base";
            default:
                return null;
        }
    }

    public static SkinPart loadSkinPart(DataInputStream stream, int version) throws IOException, InvalidCubeTypeException {
        ISkinPartType skinPartType = null;
        SkinCubeData cubeData = null;
        ArrayList<SkinMarker> markerBlocks = null;
        if (version < 6) {
            String regName = getTypeNameByLegacyId(stream.readByte());
            skinPartType = SkinPartTypes.byName(regName);
            if (skinPartType == null) {
                ModLog.error("Skin part was null");
                throw new IOException("Skin part was null");
            }
        } else {
            String regName = null;
            if (version > 12) {
                regName = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
            } else {
                regName = stream.readUTF();
            }
            if (regName.equals("armourers:skirt.base")) {
                regName = "armourers:legs.skirt";
            }
            if (regName.equals("armourers:bow.base")) {
                regName = "armourers:bow.frame1";
            }
            if (regName.equals("armourers:arrow.base")) {
                regName = "armourers:bow.arrow";
            }
            skinPartType = SkinPartTypes.byName(regName);

            if (skinPartType == null) {
                ModLog.error("Skin part was null - reg name: " + regName + " version: " + version);
                throw new IOException("Skin part was null - reg name: " + regName + " version: " + version);
            }
        }

        cubeData = new SkinCubeData(skinPartType);
        cubeData.readFromStream(stream, version, skinPartType);
        markerBlocks = new ArrayList<>();
        if (version > 8) {
            int markerCount = stream.readInt();
            for (int i = 0; i < markerCount; i++) {
                markerBlocks.add(new SkinMarker(stream, version));
            }
        }
        return new SkinPart(skinPartType, markerBlocks, cubeData);
    }

    public static void saveSkinPart(SkinPart skinPart, DataOutputStream stream) throws IOException {
        StreamUtils.writeString(stream, StandardCharsets.US_ASCII, skinPart.getType().getRegistryName().toString());
        skinPart.getCubeData().writeToStream(stream);
        stream.writeInt(skinPart.getMarkers().size());
        for (SkinMarker marker : skinPart.getMarkers()) {
            marker.writeToStream(stream);
        }
    }
}
