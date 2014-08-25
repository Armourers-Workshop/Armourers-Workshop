package riskyken.armourersWorkshop.common.customarmor;

import io.netty.buffer.ByteBuf;

public class ArmourBlockData {
    

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
}
