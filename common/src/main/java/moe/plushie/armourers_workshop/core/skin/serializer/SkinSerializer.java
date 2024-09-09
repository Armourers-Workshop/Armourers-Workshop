package moe.plushie.armourers_workshop.core.skin.serializer;

import com.google.common.collect.ImmutableList;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
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

    public static void writeToStream(Skin skin, DataOutputStream stream, SkinFileOptions options) throws IOException {
        var options1 = new SkinFileOptions();
        options1.setFileVersion(skin.getVersion());
        options1.merge(options); // merge if needed
        for (var serializer : SERIALIZERS) {
            if (serializer.isSupportedVersion(options1)) {
                if (options1.getFileVersion() >= Versions.V20) {
                    stream.writeInt(Versions.HEADER); // add the header (>=20)
                }
                stream.writeInt(serializer.getSupportedVersion());
                serializer.writeToStream(skin, IOutputStream.of(stream), options1);
                return;
            }
        }
        throw new UnsupportedFileFormatException();
    }

    public static Skin readSkinFromStream(DataInputStream stream, SkinFileOptions options) throws IOException, InvalidCubeTypeException {
        int fileVersion = stream.readInt();
        if (fileVersion == Versions.HEADER) {
            fileVersion = stream.readInt(); // read real version (>=20).
        }
        var options1 = new SkinFileOptions();
        options1.merge(options); // merge if needed
        options1.setFileVersion(fileVersion);
        for (var serializer : SERIALIZERS) {
            if (serializer.isSupportedVersion(options1)) {
                return serializer.readFromStream(IInputStream.of(stream), options1);
            }
        }
        throw new UnsupportedFileFormatException();
    }

    public static SkinFileHeader readSkinInfoFromStream(DataInputStream stream) throws IOException {
        int fileVersion = stream.readInt();
        if (fileVersion == Versions.HEADER) {
            fileVersion = stream.readInt(); // read real version (>=20).
        }
        var options1 = new SkinFileOptions();
        options1.setFileVersion(fileVersion);
        for (var serializer : SERIALIZERS) {
            if (serializer.isSupportedVersion(options1)) {
                return serializer.readInfoFromStream(IInputStream.of(stream), options1);
            }
        }
        throw new UnsupportedFileFormatException();
    }

    public static class Versions {

        private static final int HEADER = 0x534b494e; // SKIN

        public static final int V12 = SkinSerializerV12.FILE_VERSION;
        public static final int V13 = SkinSerializerV13.FILE_VERSION;
        public static final int V20 = SkinSerializerV20.FILE_VERSION;
    }
}
