package moe.plushie.armourers_workshop.core.skin.serializer.v20;

import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.ISkinSerializer;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeCoders;

import java.io.IOException;

public final class SkinSerializerV20 implements ISkinSerializer {

    public static final int FILE_HEADER = 0x534b494e; // SKIN

    public static final int FILE_VERSION = 20;
    public static final int FILE_MIN_VERSION = 20;

    public SkinSerializerV20() {
    }

    @Override
    public void writeToStream(Skin skin, IOutputStream stream, int fileVersion) throws IOException {
        // CheckedOutputStream checksum = new CheckedOutputStream(stream.stream(), new CRC32());
        // stream = IDataOutputStream.of(new DataOutputStream(checksum));
        stream.writeInt(skin.getVersion());
        stream.writeInt(0); // reserved data 1
        stream.writeInt(0); // reserved data 2
        var context = ChunkCubeCoders.createEncodeContext(skin);
        ChunkSerializers.writeToStream(skin, stream, context);
        stream.writeInt(0); // crc32
    }

    @Override
    public Skin readFromStream(IInputStream stream, int fileVersion) throws IOException {
        // read the correct file version.
        fileVersion = stream.readInt();
        stream.readInt(); // reserved data 1
        stream.readInt(); // reserved data 2
        var context = ChunkCubeCoders.createDecodeContext(fileVersion);
        return ChunkSerializers.readFromStream(stream, context);
    }

    @Override
    public SkinFileHeader readInfoFromStream(IInputStream stream, int fileVersion) throws IOException {
        // read the correct file version.
        fileVersion = stream.readInt();
        stream.readInt(); // reserved data 1
        stream.readInt(); // reserved data 2
        var context = ChunkCubeCoders.createDecodeContext(fileVersion);
        var pair = ChunkSerializers.readInfoFromStream(stream, context);
        return SkinFileHeader.optimized(fileVersion, pair.getKey(), pair.getValue());
    }

    @Override
    public int getSupportedVersion() {
        return FILE_HEADER;
    }
}
