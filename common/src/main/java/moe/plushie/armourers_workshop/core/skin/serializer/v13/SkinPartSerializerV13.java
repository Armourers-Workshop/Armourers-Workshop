package moe.plushie.armourers_workshop.core.skin.serializer.v13;

import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.geometry.collection.SkinGeometrySetV1;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.init.ModLog;

import java.io.IOException;
import java.util.ArrayList;

public final class SkinPartSerializerV13 {

    public SkinPartSerializerV13() {
    }

    public SkinPart loadSkinPart(IInputStream stream, int version) throws IOException, InvalidCubeTypeException {
        var regName = stream.readString();
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

        var geometries = SkinGeometrySetV1.readFromStream(stream, version, partType);
        var markerBlocks = new ArrayList<SkinMarker>();
        int markerCount = stream.readInt();
        for (int i = 0; i < markerCount; i++) {
            markerBlocks.add(new SkinMarker(stream));
        }

        var builder = new SkinPart.Builder(partType);
        builder.markers(markerBlocks);
        builder.geometries(geometries);
        return builder.build();
    }

    public void saveSkinPart(SkinPart skinPart, IOutputStream stream) throws IOException {
        stream.writeString(skinPart.type().registryName().toString());
        SkinGeometrySetV1.writeToStream(skinPart.geometries(), stream);
        stream.writeInt(skinPart.markers().size());
        for (var marker : skinPart.markers()) {
            marker.writeToStream(stream);
        }
    }
}
