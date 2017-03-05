package riskyken.armourersWorkshop.api.common.painting;

import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.painting.PaintType;

public interface IPantable {
    
    @Deprecated
    public void setColour(int colour);
    
    @Deprecated
    public void setColour(int colour, int side);
    
    public void setColour(byte[] rgb, int side);
    
    public void setColour(ICubeColour colour);
    
    public int getColour(int side);
    
    public void setPaintType(PaintType paintType, int side);
    
    public PaintType getPaintType(int side);
    
    public ICubeColour getColour();
}
