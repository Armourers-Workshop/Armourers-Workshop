package riskyken.armourersWorkshop.common.skin.cubes;

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
}
