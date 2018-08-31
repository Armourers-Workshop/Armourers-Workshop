package riskyken.armourers_workshop.api.common.painting;

import riskyken.armourers_workshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourers_workshop.common.painting.PaintType;

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
    
    public void setPaintType(PaintType paintType, int side);
    
    public PaintType getPaintType(int side);
    
    public ICubeColour getColour();
}
