package moe.plushie.armourers_workshop.core.skin.data.serialize.v12;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV1;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataInputStream;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModLog;

import java.io.IOException;
import java.util.ArrayList;

public final class SkinPartSerializerV12 {

    public SkinPartSerializerV12() {
    }

    public String getTypeNameByLegacyId(int legacyId) {
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

    public SkinPart loadSkinPart(IDataInputStream stream, int version) throws IOException, InvalidCubeTypeException {
        ISkinPartType partType = null;
        ArrayList<SkinMarker> markerBlocks = null;
        if (version < 6) {
            String regName = getTypeNameByLegacyId(stream.readByte());
            partType = SkinPartTypes.byName(regName);
            if (partType == null) {
                ModLog.error("Skin part was null");
                throw new IOException("Skin part was null");
            }
        } else {
            String regName = null;
            if (version > 12) {
                regName = stream.readString();
            } else {
                regName = stream.readString();
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
            partType = SkinPartTypes.byName(regName);

            if (partType == null) {
                ModLog.error("Skin part was null - reg name: " + regName + " version: " + version);
                throw new IOException("Skin part was null - reg name: " + regName + " version: " + version);
            }
        }

        SkinCubesV1 cubeData = SkinCubesV1.readFromStream(stream, version, partType);
        markerBlocks = new ArrayList<>();
        if (version > 8) {
            int markerCount = stream.readInt();
            for (int i = 0; i < markerCount; i++) {
                markerBlocks.add(new SkinMarker(stream));
            }
        }

        SkinPart.Builder builder = new SkinPart.Builder(partType);
        builder.markers(markerBlocks);
        builder.cubes(cubeData);
        return builder.build();
    }

    public void saveSkinPart(SkinPart skinPart, IDataOutputStream stream) throws IOException {
        stream.writeString(skinPart.getType().getRegistryName().toString());
        SkinCubesV1.writeToStream(skinPart.getCubeData(), stream);
        stream.writeInt(skinPart.getMarkers().size());
        for (SkinMarker marker : skinPart.getMarkers()) {
            marker.writeToStream(stream);
        }
    }
}
