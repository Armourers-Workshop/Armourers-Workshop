package moe.plushie.armourers_workshop.core.skin.serializer;

import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.serializer.exception.UnsupportedFileFormatException;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOSerializer;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v12.SkinSerializerV12;
import moe.plushie.armourers_workshop.core.skin.serializer.v13.SkinSerializerV13;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.SkinSerializerV20;
import moe.plushie.armourers_workshop.core.utils.Collections;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class SkinSerializer {

    private static final List<IOSerializer> REGISTERED_SERIALIZERS = Collections.immutableList(builder -> {
        builder.add(new SkinSerializerV20());
        builder.add(new SkinSerializerV13());
        builder.add(new SkinSerializerV12());
    });

    public static void writeToStream(Skin skin, @Nullable SkinFileOptions options, OutputStream outputStream) throws IOException {
        try (var dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream))) {
            writeToStream(skin, options, IOutputStream.of(dataOutputStream));
        }
    }

    public static void writeToStream(Skin skin, @Nullable SkinFileOptions options, IOutputStream stream) throws IOException {
        var options1 = new SkinFileOptions();
        options1.setFileVersion(skin.fileVersion());
        options1.merge(options); // merge if needed
        for (var serializer : REGISTERED_SERIALIZERS) {
            if (serializer.isSupportedVersion(options1)) {
                if (serializer.isRequiresHeader()) {
                    stream.writeInt(Versions.HEADER);
                }
                stream.writeInt(serializer.fileVersion());
                serializer.writeToStream(skin, stream, options1);
                return;
            }
        }
        throw new UnsupportedFileFormatException();
    }

    public static Skin readFromStream(@Nullable SkinFileOptions options, InputStream inputStream) throws IOException {
        try (var dataInputStream = new DataInputStream(new BufferedInputStream(inputStream))) {
            return readFromStream(options, IInputStream.of(dataInputStream));
        }
    }

    public static Skin readFromStream(@Nullable SkinFileOptions options, IInputStream stream) throws IOException {
        int fileVersion = stream.readInt();
        if (fileVersion == Versions.HEADER) {
            fileVersion = stream.readInt(); // read real version (>=20).
        }
        var options1 = new SkinFileOptions();
        options1.merge(options); // merge if needed
        options1.setFileVersion(fileVersion);
        for (var serializer : REGISTERED_SERIALIZERS) {
            if (serializer.isSupportedVersion(options1)) {
                return serializer.readFromStream(stream, options1);
            }
        }
        throw new UnsupportedFileFormatException();
    }

    public static SkinFileHeader readHeaderFromStream(InputStream inputStream) throws IOException {
        try (var dataInputStream = new DataInputStream(new BufferedInputStream(inputStream))) {
            return readHeaderFromStream(IInputStream.of(dataInputStream));
        }
    }

    public static SkinFileHeader readHeaderFromStream(IInputStream stream) throws IOException {
        int fileVersion = stream.readInt();
        if (fileVersion == Versions.HEADER) {
            fileVersion = stream.readInt(); // read real version (>=20).
        }
        var options1 = new SkinFileOptions();
        options1.setFileVersion(fileVersion);
        for (var serializer : REGISTERED_SERIALIZERS) {
            if (serializer.isSupportedVersion(options1)) {
                return serializer.readInfoFromStream(stream, options1);
            }
        }
        throw new UnsupportedFileFormatException();
    }

    public static class Versions {

        private static final int HEADER = 0x534b494e; // SKIN

        public static final int V12 = SkinSerializerV12.FILE_VERSION;
        public static final int V13 = SkinSerializerV13.FILE_VERSION;
        public static final int V20 = SkinSerializerV20.FILE_MIN_VERSION;

        public static final int LATEST = SkinSerializerV20.FILE_LATEST_VERSION;
    }
}
