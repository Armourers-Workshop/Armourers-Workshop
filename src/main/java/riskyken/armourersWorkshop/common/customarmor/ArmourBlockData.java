package riskyken.armourersWorkshop.common.customarmor;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class ArmourBlockData {
    
    private static final String TAG_X = "x";
    private static final String TAG_Y = "y";
    private static final String TAG_Z = "z";
    private static final String TAG_COLOUR = "colour";
    private static final String TAG_GLOWING = "glowing";
    
    @Override
    public String toString() {
        return "ArmourBlockData [x=" + x + ", y=" + y + ", z=" + z
                + ", colour=" + colour + ", glowing=" + glowing + "]";
    }
    
    public byte x;
    public byte y;
    public byte z;
    public int colour;
    public boolean glowing;
    
    public ArmourBlockData() {
    }
    
    public ArmourBlockData(int x, int y, int z, int colour, boolean glowing) {
        this.x = (byte) x;
        this.y = (byte) y;
        this.z = (byte) z;
        this.colour = colour;
        this.glowing = glowing;
    }
    
    public ArmourBlockData(byte x, byte y, byte z, int colour, boolean glowing) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.colour = colour;
        this.glowing = glowing;
    }
    
    public ArmourBlockData(ByteBuf buf) {
        readFromBuf(buf);
    }
    
    public ArmourBlockData(NBTTagCompound compound) {
        readFromNBT(compound);
    }
    
    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(x);
        buf.writeByte(y);
        buf.writeByte(z);
        buf.writeInt(colour);
        buf.writeBoolean(glowing);
    }
    
    private void readFromBuf(ByteBuf buf) {
        x = buf.readByte();
        y = buf.readByte();
        z = buf.readByte();
        colour = buf.readInt();
        glowing = buf.readBoolean();
    }
    
    public void writeToNBT(NBTTagCompound compound) {
        compound.setByte(TAG_X, x);
        compound.setByte(TAG_Y, y);
        compound.setByte(TAG_Z, z);
        compound.setInteger(TAG_COLOUR, colour);
        compound.setBoolean(TAG_GLOWING, glowing);
    }
    
    private void readFromNBT(NBTTagCompound compound) {
        x = compound.getByte(TAG_X);
        y = compound.getByte(TAG_Y);
        z = compound.getByte(TAG_Z);
        colour = compound.getInteger(TAG_COLOUR);
        glowing = compound.getBoolean(TAG_GLOWING);
    }
}
