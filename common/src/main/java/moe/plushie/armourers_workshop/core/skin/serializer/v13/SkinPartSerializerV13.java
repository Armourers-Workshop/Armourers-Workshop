package moe.plushie.armourers_workshop.core.skin.serializer.v13;

import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV1;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.init.ModLog;

import java.io.IOException;
import java.util.ArrayList;

public final class SkinPartSerializerV13 {

    public SkinPartSerializerV13() {
    }

    public SkinPart loadSkinPart(IInputStream stream, int version) throws IOException, InvalidCubeTypeException {
        String regName = stream.readString();
        if (regName.equals("armourers:skirt.base")) {
            regName = "armourers:legs.skirt";
        }
        if (regName.equals("armourers:bow.base")) {
            regName = "armourers:bow.frame1";
        }
        if (regName.equals("armourers:arrow.base")) {
            regName = "armourers:bow.arrow";
        }
        var partType = SkinPartTypes.byName(regName);
        if (partType == null) {
            ModLog.error("Skin part was null - reg name: " + regName + " version: " + version);
            throw new IOException("Skin part was null - reg name: " + regName + " version: " + version);
        }

        var cubeData = SkinCubesV1.readFromStream(stream, version, partType);
        var markerBlocks = new ArrayList<SkinMarker>();
        int markerCount = stream.readInt();
        for (int i = 0; i < markerCount; i++) {
            markerBlocks.add(new SkinMarker(stream));
        }

        var builder = new SkinPart.Builder(partType);
        builder.markers(markerBlocks);
        builder.cubes(cubeData);
        return builder.build();
    }

    public void saveSkinPart(SkinPart skinPart, IOutputStream stream) throws IOException {
        stream.writeString(skinPart.getType().getRegistryName().toString());
        SkinCubesV1.writeToStream(skinPart.getCubeData(), stream);
        stream.writeInt(skinPart.getMarkers().size());
        for (var marker : skinPart.getMarkers()) {
            marker.writeToStream(stream);
        }
    }
}
