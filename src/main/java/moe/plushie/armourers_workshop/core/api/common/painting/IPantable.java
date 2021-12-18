package moe.plushie.armourers_workshop.core.api.common.painting;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.common.skin.ICubeColour;

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
    
    public void setPaintType(ISkinPaintType paintType, int side);
    
    public ISkinPaintType getPaintType(int side);
    
    public ICubeColour getColour();
}
