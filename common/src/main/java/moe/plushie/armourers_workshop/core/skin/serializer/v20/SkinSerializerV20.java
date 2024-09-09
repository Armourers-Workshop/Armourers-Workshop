package moe.plushie.armourers_workshop.core.skin.serializer.v20;

import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.ISkinSerializer;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeCoders;

import java.io.IOException;

public final class SkinSerializerV20 implements ISkinSerializer {

    public static final int FILE_VERSION = 20;
    public static final int FILE_MIN_VERSION = 20;

    public SkinSerializerV20() {
    }

    @Override
    public void writeToStream(Skin skin, IOutputStream stream, SkinFileOptions options) throws IOException {
        // CheckedOutputStream checksum = new CheckedOutputStream(stream.stream(), new CRC32());
        // stream = IDataOutputStream.of(new DataOutputStream(checksum));
        stream.writeInt(0); // reserved data 1
        stream.writeInt(0); // reserved data 2
        var context = ChunkCubeCoders.createEncodeContext(skin, options);
        ChunkSerializers.writeToStream(skin, stream, context);
        stream.writeInt(0); // crc32
    }

    @Override
    public Skin readFromStream(IInputStream stream, SkinFileOptions options) throws IOException {
        stream.readInt(); // reserved data 1
        stream.readInt(); // reserved data 2
        var context = ChunkCubeCoders.createDecodeContext(options);
        return ChunkSerializers.readFromStream(stream, context);
    }

    @Override
    public SkinFileHeader readInfoFromStream(IInputStream stream, SkinFileOptions options) throws IOException {
        stream.readInt(); // reserved data 1
        stream.readInt(); // reserved data 2
        var context = ChunkCubeCoders.createDecodeContext(options);
        var pair = ChunkSerializers.readInfoFromStream(stream, context);
        return SkinFileHeader.optimized(options.getFileVersion(), pair.getKey(), pair.getValue());
    }

    @Override
    public boolean isSupportedVersion(SkinFileOptions options) {
        return options.getFileVersion() >= FILE_MIN_VERSION;
    }

    @Override
    public int getSupportedVersion() {
        return FILE_VERSION;
    }
}
