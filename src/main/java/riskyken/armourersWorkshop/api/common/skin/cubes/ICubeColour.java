package riskyken.armourersWorkshop.api.common.skin.cubes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public interface ICubeColour {
    
    public byte getRed(EnumFacing side);
    
    public byte getGreen(EnumFacing side);
        
    public byte getBlue(EnumFacing side);
    
    public byte getPaintType(EnumFacing side);
    
    public byte[] getRed();
    
    public byte[] getGreen();
    
    public byte[] getBlue();
    
    public byte[] getPaintType();
    
    public void setColour(int colour, EnumFacing side);
    
    @Deprecated
    public void setColour(int colour);
    
    public void setRed(byte red, EnumFacing side);
    
    public void setGreen(byte green, EnumFacing side);
    
    public void setBlue(byte blue, EnumFacing side);
    
    public void setPaintType(byte type, EnumFacing side);
    
    public void readFromNBT(NBTTagCompound compound);
    
    public void writeToNBT(NBTTagCompound compound);
}
