package riskyken.armourersWorkshop.common.equipment.cubes;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPart;
import riskyken.armourersWorkshop.utils.ModLogger;

public class Cube implements ICube {

    protected static final String TAG_ID = "id";
    protected static final String TAG_X = "x";
    protected static final String TAG_Y = "y";
    protected static final String TAG_Z = "z";
    protected static final String TAG_COLOUR = "colour";
    
    protected byte id = -1;
    protected byte x;
    protected byte y;
    protected byte z;
    protected int colour;
    protected BitSet faceFlags;
    
    @Override
    public byte getX() {
        return x;
    }

    @Override
    public byte getY() {
        return y;
    }

    @Override
    public byte getZ() {
        return z;
    }
    
    @Override
    public void setX(byte x) {
        this.x = x;
    }
    
    @Override
    public void setY(byte y) {
        this.y = y;
    }
    
    @Override
    public void setZ(byte z) {
        this.z = z;
    }
    
    @Override
    public BitSet getFaceFlags() {
        return faceFlags;
    }
    
    @Override
    public void setFaceFlags(BitSet faceFlags) {
        this.faceFlags = faceFlags;
    }
    
    @Override
    public int getColour() {
        return colour;
    }
    
    @Override
    public void setColour(int colour) {
        this.colour = colour;
    }
    
    @Override
    public boolean isGlowing() {
        return false;
    }
    
    @Override
    public boolean needsPostRender() {
        return false;
    }
    
    @Override
    public void setId(byte id) {
        if (this.id != -1) {
            ModLogger.log(Level.WARN, "Resetting cube id.");
        }
        this.id = id;
    }
    
    @Override
    public byte getId() {
        return id;
    }
    
    @Override
    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(id);
        buf.writeByte(x);
        buf.writeByte(y);
        buf.writeByte(z);
        buf.writeInt(colour);
    }
    
    @Override
    public void readFromBuf(ByteBuf buf) {
        //id = buf.readByte();
        x = buf.readByte();
        y = buf.readByte();
        z = buf.readByte();
        colour = buf.readInt();
    }
    
    @Override
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(id);
        stream.writeByte(x);
        stream.writeByte(y);
        stream.writeByte(z);
        stream.writeInt(colour);
    }
    
    @Override
    public void readFromStream(DataInputStream stream, int version, IEquipmentSkinPart skinPart) throws IOException {
        //id = stream.readByte();
        x = stream.readByte();
        y = stream.readByte();
        z = stream.readByte();
        colour = stream.readInt();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + colour;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }
    
    @Override
    public String toString() {
        return "CustomEquipmentBlockData [x=" + x + ", y=" + y + ", z=" + z
        + ", colour=" + colour + ", blockType=" + id + "]";
    }
}
