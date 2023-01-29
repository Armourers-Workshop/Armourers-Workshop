package moe.plushie.armourers_workshop.core.skin.data.serialize.v20;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataInputStream;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataSerializer;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;

public final class SkinSerializerV20 implements IDataSerializer {

    public static final int FILE_HEADER = 0x534b494e;

    public static final int FILE_VERSION = 20;
    public static final int MAX_FILE_VERSION = 20;

    public SkinSerializerV20() {
    }

    @Override
    public void writeToStream(Skin skin, IDataOutputStream stream, int fileVersion) throws IOException {
        // CheckedOutputStream checksum = new CheckedOutputStream(stream.stream(), new CRC32());
        // stream = IDataOutputStream.of(new DataOutputStream(checksum));
        stream.writeInt(FILE_HEADER);
        stream.writeInt(FILE_VERSION);
        stream.writeInt(0); // reserved data 1
        stream.writeInt(0); // reserved data 2
        ChunkSerializers.writeToStream(skin, stream);
        stream.writeInt(0); // crc32
    }

    @Override
    public Skin readFromStream(IDataInputStream stream, int fileVersion) throws IOException, InvalidCubeTypeException {
        // read the correct file version.
        fileVersion = stream.readInt();
        stream.readInt();
        stream.readInt();
        return ChunkSerializers.readFromStream(stream);
    }

    @Override
    public SkinFileHeader readInfoFromStream(IDataInputStream stream, int fileVersion) throws IOException {
        // read the correct file version.
        fileVersion = stream.readInt();
        stream.readInt();
        stream.readInt();
        Pair<ISkinType, ISkinProperties> pair = ChunkSerializers.readInfoFromStream(stream);
        return SkinFileHeader.of(fileVersion, pair.getKey(), pair.getValue());
    }

    @Override
    public int getSupportedVersion() {
        return FILE_HEADER;
    }
}
