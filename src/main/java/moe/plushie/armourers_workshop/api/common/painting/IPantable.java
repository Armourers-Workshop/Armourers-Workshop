package moe.plushie.armourers_workshop.api.common.painting;

import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;

public interface IPantable {
    
    /** @deprecated Replaced by {@link #setColour(byte[] rgb, int side)} */
    @Deprecated
    public void setColour(int colour);
    
    /** @deprecated Replaced by {@link #setColour(byte[] rgb, int side)} */
    @Deprecated
    public void setColour(int colour, int side);
    
    public void setColour(byte[] rgb, int side);
    
    public void setColour(ICubeColour colour);
    
    public int getColour(int side);
    
    public void setPaintType(IPaintType paintType, int side);
    
    public IPaintType getPaintType(int side);
    
    public ICubeColour getColour();
}
