package moe.plushie.armourers_workshop.common.skin.cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CubeMarkerData {
    
    public byte x;
    public byte y;
    public byte z;
    public byte meta;
    
    public CubeMarkerData(byte x, byte y, byte z, byte meta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.meta = meta;
    }
    
    public CubeMarkerData(DataInputStream stream, int version) throws IOException {
        readFromStream(stream, version);
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
