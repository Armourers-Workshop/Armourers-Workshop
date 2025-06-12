package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.skin.ISkinMarker;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class SkinMarker implements ISkinMarker {

    public static final IDataCodec<SkinMarker> CODEC = IDataCodec.LONG.xmap(SkinMarker::of, SkinMarker::asLong);

    public final byte x;
    public final byte y;
    public final byte z;
    public final byte meta;

    public SkinMarker(byte x, byte y, byte z, byte meta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.meta = meta;
    }

    public SkinMarker(IInputStream stream) throws IOException {
        this.x = stream.readByte();
        this.y = stream.readByte();
        this.z = stream.readByte();
        this.meta = stream.readByte();
    }

    public static SkinMarker of(long value) {
        long x = (value >> 16) & 0xff;
        long y = (value >> 8) & 0xff;
        long z = (value) & 0xff;
        long m = (value >> 24) & 0xff;
        return new SkinMarker((byte) x, (byte) y, (byte) z, (byte) m);
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeByte(x);
        stream.writeByte(y);
        stream.writeByte(z);
        stream.writeByte(meta);
    }

    public long asLong() {
        return (long) (meta & 0xff) << 24 | (x & 0xff) << 16 | (y & 0xff) << 8 | (z & 0xff);
    }

    @Override
    public OpenVector3i position() {
        return new OpenVector3i(x, y, z);
    }

    @Nullable
    @Override
    public OpenDirection direction() {
        if (meta != 0) {
            return OpenDirection.from3DDataValue(meta - 1);
        }
        return null;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "pos", position(), "direction", direction());
    }

}
