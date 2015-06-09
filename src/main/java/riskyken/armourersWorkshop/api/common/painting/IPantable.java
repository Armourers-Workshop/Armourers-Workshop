package riskyken.armourersWorkshop.api.common.painting;

import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;

public interface IPantable {

    public void setColour(int colour);
    
    public void setColour(int colour, int side);
    
    public void setColour(ICubeColour colour);
    
    public int getColour(int side);
    
    public ICubeColour getColour();
}
