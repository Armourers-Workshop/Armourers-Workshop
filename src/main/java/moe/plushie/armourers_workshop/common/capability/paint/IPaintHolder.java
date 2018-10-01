package moe.plushie.armourers_workshop.common.capability.paint;

import moe.plushie.armourers_workshop.common.painting.PaintType;

public interface IPaintHolder {

    public void setPaintColour(byte[] colour, int side);
    
    public byte[] getPaintColour(int side);
    
    public void setPaintType(PaintType paintType, int side);
    
    public PaintType getPaintType(int side);
}
