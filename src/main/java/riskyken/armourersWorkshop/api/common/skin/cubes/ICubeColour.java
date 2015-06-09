package riskyken.armourersWorkshop.api.common.skin.cubes;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

public interface ICubeColour {
    
    public byte getRed(int side);
    
    public byte getGreen(int side);
        
    public byte getBlue(int side);
    
    public byte[] getRed();
    
    public byte[] getGreen();
    
    public byte[] getBlue();
    
    public void setColour(int colour, int side);
    
    @Deprecated
    public void setColour(int colour);
    
    public void setRed(byte red, int side);
    
    public void setGreen(byte green, int side);
    
    public void setBlue(byte blue, int side);
    
    public void setRed(byte[] red);
    
    public void setGreen(byte[] green);
    
    public void setBlue(byte[] blue);
    
    public void readFromNBT(NBTTagCompound compound);
    
    public void writeToNBT(NBTTagCompound compound);
    
    public void readFromBuf(ByteBuf buf);
    
    public void writeToBuf(ByteBuf buf);
    
    public void readFromStream(DataInputStream stream, int version) throws IOException;
    
    public void writeToStream(DataOutputStream stream) throws IOException;
}
