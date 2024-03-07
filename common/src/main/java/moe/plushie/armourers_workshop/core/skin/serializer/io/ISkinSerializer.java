package moe.plushie.armourers_workshop.core.skin.serializer.io;

import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileHeader;

import java.io.IOException;

public interface ISkinSerializer {

    void writeToStream(Skin skin, IOutputStream stream, int fileVersion) throws IOException;

    Skin readFromStream(IInputStream stream, int fileVersion) throws IOException, InvalidCubeTypeException;

    SkinFileHeader readInfoFromStream(IInputStream stream, int fileVersion) throws IOException;

    int getSupportedVersion();

    default boolean isSupportedVersion(int fileVersion) {
        return getSupportedVersion() == fileVersion;
    }
}
