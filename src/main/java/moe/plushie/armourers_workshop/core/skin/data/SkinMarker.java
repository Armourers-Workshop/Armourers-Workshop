package moe.plushie.armourers_workshop.core.skin.data;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinMarker;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3i;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SkinMarker implements ISkinMarker {
    
    public byte x;
    public byte y;
    public byte z;
    public byte meta;
    
    public SkinMarker(byte x, byte y, byte z, byte meta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.meta = meta;
    }
    
    public SkinMarker(DataInputStream stream, int version) throws IOException {
        readFromStream(stream, version);
    }

    @Override
    public Vector3i getPosition() {
        return new Vector3i(x, y, z);
    }

    @Override
    public Direction getDirection() {
        return Direction.values()[meta - 1];
    }

    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(x);
        stream.writeByte(y);
        stream.writeByte(z);
        stream.writeByte(meta);
    }
    
    private void readFromStream(DataInputStream stream, int version) throws IOException {
        x = stream.readByte();
        y = stream.readByte();
        z = stream.readByte();
        meta = stream.readByte();
    }

    @Override
    public String toString() {
        return "CubeMarkerData [x=" + x + ", y=" + y + ", z=" + z + ", meta=" + meta + "]";
    }
    
    
}
