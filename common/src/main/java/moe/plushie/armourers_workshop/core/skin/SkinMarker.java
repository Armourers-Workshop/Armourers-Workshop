package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.skin.ISkinMarker;
import moe.plushie.armourers_workshop.core.data.OptionalDirection;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class SkinMarker implements ISkinMarker {

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
    public Vector3i getPosition() {
        return new Vector3i(x, y, z);
    }

    @Nullable
    @Override
    public Direction getDirection() {
        return OptionalDirection.values()[meta].getDirection();
    }

    @Override
    public String toString() {
        return ObjectUtils.makeDescription(this, "pos", getPosition(), "direction", getDirection());
    }

}
