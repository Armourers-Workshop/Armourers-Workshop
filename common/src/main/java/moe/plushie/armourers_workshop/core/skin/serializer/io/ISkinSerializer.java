package moe.plushie.armourers_workshop.core.skin.serializer.io;

import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;

import java.io.IOException;

public interface ISkinSerializer {

    void writeToStream(Skin skin, IOutputStream stream, SkinFileOptions options) throws IOException;

    Skin readFromStream(IInputStream stream, SkinFileOptions options) throws IOException, InvalidCubeTypeException;

    SkinFileHeader readInfoFromStream(IInputStream stream, SkinFileOptions options) throws IOException;

    int getSupportedVersion();

    boolean isSupportedVersion(SkinFileOptions options);
}
