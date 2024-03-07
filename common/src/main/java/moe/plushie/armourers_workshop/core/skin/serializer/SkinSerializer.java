package moe.plushie.armourers_workshop.core.skin.serializer;

import com.google.common.collect.ImmutableList;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.exception.NewerFileVersionException;
import moe.plushie.armourers_workshop.core.skin.exception.UnsupportedFileFormatException;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.ISkinSerializer;
import moe.plushie.armourers_workshop.core.skin.serializer.v12.SkinSerializerV12;
import moe.plushie.armourers_workshop.core.skin.serializer.v13.SkinSerializerV13;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.SkinSerializerV20;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SkinSerializer {

    private static final ImmutableList<ISkinSerializer> SERIALIZERS = ImmutableList.<ISkinSerializer>builder()
            .add(new SkinSerializerV20())
            .add(new SkinSerializerV13())
            .add(new SkinSerializerV12())
            .build();

    public static void writeToStream(Skin skin, DataOutputStream stream) throws IOException {
        int fileVersion = skin.getVersion();
        if (fileVersion >= SkinSerializerV20.FILE_MIN_VERSION) {
            fileVersion = SkinSerializerV20.FILE_HEADER;
        }
        writeToStream(skin, stream, fileVersion);
    }

    public static void writeToStream(Skin skin, DataOutputStream stream, int fileVersion) throws IOException {
        for (ISkinSerializer impl : SERIALIZERS) {
            if (impl.isSupportedVersion(fileVersion)) {
                stream.writeInt(impl.getSupportedVersion());
                impl.writeToStream(skin, IOutputStream.of(stream), fileVersion);
                return;
            }
        }
        throw new UnsupportedFileFormatException();
    }

    public static Skin readSkinFromStream(DataInputStream stream) throws IOException, NewerFileVersionException, InvalidCubeTypeException {
        int fileVersion = stream.readInt();
        for (ISkinSerializer impl : SERIALIZERS) {
            if (impl.isSupportedVersion(fileVersion)) {
                return impl.readFromStream(IInputStream.of(stream), fileVersion);
            }
        }
        throw new NewerFileVersionException(fileVersion);
    }

    public static SkinFileHeader readSkinInfoFromStream(DataInputStream stream) throws IOException, NewerFileVersionException {
        int fileVersion = stream.readInt();
        for (ISkinSerializer impl : SERIALIZERS) {
            if (impl.isSupportedVersion(fileVersion)) {
                return impl.readInfoFromStream(IInputStream.of(stream), fileVersion);
            }
        }
        throw new NewerFileVersionException(fileVersion);
    }

    public static class Versions {

        public static final int V12 = SkinSerializerV12.FILE_VERSION;
        public static final int V13 = SkinSerializerV13.FILE_VERSION;
        public static final int V20 = SkinSerializerV20.FILE_VERSION;
    }
}
