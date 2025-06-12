package moe.plushie.armourers_workshop.core.skin.serializer.v20;

import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOSerializer;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.ChunkGeometrySerializers;

import java.io.IOException;

public final class SkinSerializerV20 implements IOSerializer {

    public static final int FILE_MIN_VERSION = 20;
    public static final int FILE_LATEST_VERSION = 25;

    public SkinSerializerV20() {
    }

    @Override
    public void writeToStream(Skin skin, IOutputStream stream, SkinFileOptions options) throws IOException {
        // var checksum = new CheckedOutputStream(stream.stream(), new CRC32());
        // stream = IOutputStream.of(new DataOutputStream(checksum));
        stream.writeInt(0); // reserved data 1
        stream.writeInt(0); // reserved data 2
        var context = ChunkGeometrySerializers.createEncodeContext(skin, options);
        ChunkSerializers.writeToStream(skin, stream, context);
        stream.writeInt(0); // crc32
    }

    @Override
    public Skin readFromStream(IInputStream stream, SkinFileOptions options) throws IOException {
        stream.readInt(); // reserved data 1
        stream.readInt(); // reserved data 2
        var context = ChunkGeometrySerializers.createDecodeContext(options);
        return ChunkSerializers.readFromStream(stream, context);
    }

    @Override
    public SkinFileHeader readInfoFromStream(IInputStream stream, SkinFileOptions options) throws IOException {
        stream.readInt(); // reserved data 1
        stream.readInt(); // reserved data 2
        var context = ChunkGeometrySerializers.createDecodeContext(options);
        var pair = ChunkSerializers.readInfoFromStream(stream, context);
        return SkinFileHeader.optimized(options.fileVersion(), pair.getKey(), pair.getValue());
    }

    @Override
    public boolean isSupportedVersion(SkinFileOptions options) {
        return options.fileVersion() >= FILE_MIN_VERSION;
    }

    @Override
    public int fileVersion() {
        return FILE_LATEST_VERSION;
    }

    @Override
    public boolean isRequiresHeader() {
        return true;
    }
}
