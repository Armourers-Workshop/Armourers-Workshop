package moe.plushie.armourers_workshop.core.skin.data.base;

import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;

import java.io.IOException;

public interface IDataSerializer {

    void writeToStream(Skin skin, IDataOutputStream stream, int fileVersion) throws IOException;

    Skin readFromStream(IDataInputStream stream, int fileVersion) throws IOException, InvalidCubeTypeException;

    SkinFileHeader readInfoFromStream(IDataInputStream stream, int fileVersion) throws IOException;

    int getSupportedVersion();

    default boolean isSupportedVersion(int fileVersion) {
        return getSupportedVersion() == fileVersion;
    }
}
