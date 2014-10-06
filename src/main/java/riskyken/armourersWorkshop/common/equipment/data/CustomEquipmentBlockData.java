package riskyken.armourersWorkshop.common.equipment.data;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

import net.minecraft.nbt.NBTTagCompound;

public class CustomEquipmentBlockData {
    
    private static final String TAG_X = "x";
    private static final String TAG_Y = "y";
    private static final String TAG_Z = "z";
    private static final String TAG_COLOUR = "colour";
    private static final String TAG_BLOCK_TYPE = "blockType";
    
    @Override
    public String toString() {
        return "CustomEquipmentBlockData [x=" + x + ", y=" + y + ", z=" + z
                + ", colour=" + colour + ", blockType=" + blockType + "]";
    }
    
    public byte x;
    public byte y;
    public byte z;
    public int colour;
    public byte blockType;
    public BitSet faceFlags = null;
    
    public CustomEquipmentBlockData() {
    }
    
    public CustomEquipmentBlockData(int x, int y, int z, int colour, int blockType) {
        this.x = (byte) x;
        this.y = (byte) y;
        this.z = (byte) z;
        this.colour = colour;
        this.blockType = (byte) blockType;
    }
    
    public CustomEquipmentBlockData(byte x, byte y, byte z, int colour, byte blockType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.colour = colour;
        this.blockType = blockType;
    }
    
    public CustomEquipmentBlockData(ByteBuf buf) {
        readFromBuf(buf);
    }
    
    public CustomEquipmentBlockData(NBTTagCompound compound) {
        readFromNBT(compound);
    }
    
    public CustomEquipmentBlockData(DataInputStream stream) throws IOException {
        readFromStream(stream);
    }

    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(x);
        buf.writeByte(y);
        buf.writeByte(z);
        buf.writeInt(colour);
        buf.writeByte(blockType);
    }
    
    private void readFromBuf(ByteBuf buf) {
        x = buf.readByte();
        y = buf.readByte();
        z = buf.readByte();
        colour = buf.readInt();
        blockType = buf.readByte();
    }
    
    public void writeToNBT(NBTTagCompound compound) {
        compound.setByte(TAG_X, x);
        compound.setByte(TAG_Y, y);
        compound.setByte(TAG_Z, z);
        compound.setInteger(TAG_COLOUR, colour);
        compound.setByte(TAG_BLOCK_TYPE, blockType);
    }
    
    private void readFromNBT(NBTTagCompound compound) {
        x = compound.getByte(TAG_X);
        y = compound.getByte(TAG_Y);
        z = compound.getByte(TAG_Z);
        colour = compound.getInteger(TAG_COLOUR);
        blockType = compound.getByte(TAG_BLOCK_TYPE);
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(x);
        stream.writeByte(y);
        stream.writeByte(z);
        stream.writeInt(colour);
        stream.writeByte(blockType);
    }
    
    private void readFromStream(DataInputStream stream) throws IOException {
        x = stream.readByte();
        y = stream.readByte();
        z = stream.readByte();
        colour = stream.readInt();
        blockType = stream.readByte();
    }
    
    public boolean isGlowing() {
        return this.blockType == 1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + blockType;
        result = prime * result + colour;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomEquipmentBlockData other = (CustomEquipmentBlockData) obj;
        if (blockType != other.blockType)
            return false;
        if (colour != other.colour)
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        if (z != other.z)
            return false;
        return true;
    }
}
