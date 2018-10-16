package moe.plushie.armourers_workshop.common.capability.paint;

import moe.plushie.armourers_workshop.common.painting.PaintType;
import net.minecraft.util.EnumFacing;

public interface IPaintHolder {

    public void setPaintColour(byte[] colour, EnumFacing facing);
    
    public byte[] getPaintColour(EnumFacing facing);
    
    public void setPaintType(PaintType paintType, EnumFacing facing);
    
    public PaintType getPaintType(EnumFacing facing);
}
