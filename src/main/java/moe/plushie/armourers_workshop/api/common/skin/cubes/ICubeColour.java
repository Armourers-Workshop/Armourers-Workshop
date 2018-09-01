package moe.plushie.armourers_workshop.api.common.skin.cubes;

import net.minecraft.nbt.NBTTagCompound;

public interface ICubeColour {
    
    public byte getRed(int side);
    
    public byte getGreen(int side);
        
    public byte getBlue(int side);
    
    public byte getPaintType(int side);
    
    public byte[] getRed();
    
    public byte[] getGreen();
    
    public byte[] getBlue();
    
    public byte[] getPaintType();
    
    public void setColour(int colour, int side);
    
    @Deprecated
    public void setColour(int colour);
    
    public void setRed(byte red, int side);
    
    public void setGreen(byte green, int side);
    
    public void setBlue(byte blue, int side);
    
    public void setPaintType(byte type, int side);
    
    public void readFromNBT(NBTTagCompound compound);
    
    public void writeToNBT(NBTTagCompound compound);
}
