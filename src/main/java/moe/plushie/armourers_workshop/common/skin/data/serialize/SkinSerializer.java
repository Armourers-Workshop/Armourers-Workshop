package moe.plushie.armourers_workshop.common.skin.data.serialize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.common.exception.NewerFileVersionException;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.serialize.v13.SkinSerializerV13;

public class SkinSerializer {
    
    public static void writeToStream(Skin skin, DataOutputStream stream) throws IOException {
        SkinSerializerV13.writeToStream(skin, stream);
    }
    
    public static Skin readSkinFromStream(DataInputStream stream) throws IOException, NewerFileVersionException, InvalidCubeTypeException {
        return SkinSerializerV13.readSkinFromStream(stream);
    }
    
    public static ISkinType readSkinTypeNameFromStream(DataInputStream stream) throws IOException, NewerFileVersionException {
        return SkinSerializerV13.readSkinTypeNameFromStream(stream);
    }
}
