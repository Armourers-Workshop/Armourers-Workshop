package moe.plushie.armourers_workshop.common.skin.data.serialize.v13;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeMarkerData;
import moe.plushie.armourers_workshop.common.skin.data.SkinCubeData;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.StreamUtils;

public final class SkinPartSerializerV13 {
    
    private SkinPartSerializerV13() {}
    
    public static SkinPart loadSkinPart(DataInputStream stream, int version) throws IOException, InvalidCubeTypeException {
        ISkinPartType skinPart = null;
        SkinCubeData cubeData = null;
        ArrayList<CubeMarkerData> markerBlocks = null;
        String regName = StreamUtils.readString(stream, Charsets.US_ASCII);
        if (regName.equals("armourers:skirt.base")) {
            regName = "armourers:legs.skirt";
        }
        if (regName.equals("armourers:bow.base")) {
            regName = "armourers:bow.frame1";
        }
        if (regName.equals("armourers:arrow.base")) {
            regName = "armourers:bow.arrow";
        }
        skinPart = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(regName);
        if (skinPart == null) {
            ModLogger.log(Level.ERROR, "Skin part was null - reg name: " + regName + " version: " + version);
            throw new IOException("Skin part was null - reg name: " + regName + " version: " + version);
        }
        
        cubeData = new SkinCubeData();
        cubeData.readFromStream(stream, version, skinPart);
        markerBlocks = new ArrayList<CubeMarkerData>();
        int markerCount = stream.readInt();
        for (int i = 0; i < markerCount; i++) {
            markerBlocks.add(new CubeMarkerData(stream, version));
        }
        return new SkinPart(cubeData, skinPart, markerBlocks);
    }
    
    public static void saveSkinPart(SkinPart skinPart, DataOutputStream stream) throws IOException {
        StreamUtils.writeString(stream, Charsets.US_ASCII, skinPart.getPartType().getRegistryName());
        skinPart.getCubeData().writeToStream(stream);
        stream.writeInt(skinPart.getMarkerCount());
        for (int i = 0; i < skinPart.getMarkerCount(); i++) {
            skinPart.getMarkerBlocks().get(i).writeToStream(stream);
        }
    }
}
