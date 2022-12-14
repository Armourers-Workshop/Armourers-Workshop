package moe.plushie.armourers_workshop.core.skin.data.serialize.v14;

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

public final class SkinPartSerializerV14 {

    private SkinPartSerializerV14() {
    }

    public static SkinPart loadSkinPart(DataInputStream stream, int version) throws IOException, InvalidCubeTypeException {
        ISkinPartType skinPartType = null;
        SkinCubeData cubeData = null;
        ArrayList<SkinMarker> markerBlocks = null;
        String regName = StreamUtils.readString(stream, StandardCharsets.US_ASCII);
        skinPartType = SkinPartTypes.byName(regName);
        if (skinPartType == null) {
            ModLog.error("Skin part was null - reg name: " + regName + " version: " + version);
            throw new IOException("Skin part was null - reg name: " + regName + " version: " + version);
        }

        cubeData = new SkinCubeData(skinPartType);
        cubeData.readFromStream(stream, version, skinPartType);
        markerBlocks = new ArrayList<>();
        int markerCount = stream.readInt();
        for (int i = 0; i < markerCount; i++) {
            markerBlocks.add(new SkinMarker(stream, version));
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
