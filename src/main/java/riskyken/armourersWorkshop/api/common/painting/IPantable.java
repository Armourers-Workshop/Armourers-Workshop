package riskyken.armourersWorkshop.api.common.painting;

import net.minecraft.util.EnumFacing;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.painting.PaintType;

public interface IPantable {
    
    @Deprecated
    public void setColour(int colour);
    
    @Deprecated
    public void setColour(int colour, EnumFacing side);
    
    public void setColour(byte[] rgb, EnumFacing side);
    
    public void setColour(ICubeColour colour);
    
    public int getColour(EnumFacing side);
    
    public void setPaintType(PaintType paintType, EnumFacing side);
    
    public PaintType getPaintType(EnumFacing side);
    
    public ICubeColour getColour();
}
