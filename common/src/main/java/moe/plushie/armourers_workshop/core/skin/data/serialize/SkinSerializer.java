package moe.plushie.armourers_workshop.core.skin.data.serialize;

import com.google.common.collect.ImmutableList;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataInputStream;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataSerializer;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v12.SkinSerializerV12;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v13.SkinSerializerV13;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.SkinSerializerV20;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.exception.NewerFileVersionException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SkinSerializer {

    public static final int FILE_VERSION_V1M = 13; // the v1 max version

    private static final ImmutableList<IDataSerializer> PROVIDERS = ImmutableList.<IDataSerializer>builder()
//            .add(new SkinSerializerV20())
            .add(new SkinSerializerV13())
            .add(new SkinSerializerV12())
            .build();

    public static void writeToStream(Skin skin, DataOutputStream stream) throws IOException {
        if (skin.requiresAdvanceFeatures()) {
            // the file version is equal the file header,
            // in the new design, file versions become irrelevant.
            writeToStream(skin, stream, SkinSerializerV20.FILE_HEADER);
        } else {
            writeToStream(skin, stream, FILE_VERSION_V1M);
        }
    }

    public static void writeToStream(Skin skin, DataOutputStream stream, int fileVersion) throws IOException {
        for (IDataSerializer impl : PROVIDERS) {
            if (impl.isSupportedVersion(fileVersion)) {
                impl.writeToStream(skin, IDataOutputStream.of(stream), fileVersion);
                break;
            }
        }
    }

    public static Skin readSkinFromStream(DataInputStream stream) throws IOException, NewerFileVersionException, InvalidCubeTypeException {
        int fileVersion = stream.readInt();
        for (IDataSerializer impl : PROVIDERS) {
            if (impl.isSupportedVersion(fileVersion)) {
                return impl.readFromStream(IDataInputStream.of(stream), fileVersion);
            }
        }
        throw new NewerFileVersionException();
    }

    public static SkinFileHeader readSkinInfoFromStream(DataInputStream stream) throws IOException, NewerFileVersionException {
        int fileVersion = stream.readInt();
        for (IDataSerializer impl : PROVIDERS) {
            if (impl.isSupportedVersion(fileVersion)) {
                return impl.readInfoFromStream(IDataInputStream.of(stream), fileVersion);
            }
        }
        throw new NewerFileVersionException();
    }
}
