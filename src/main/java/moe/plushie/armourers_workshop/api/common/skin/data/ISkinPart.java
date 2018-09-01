package moe.plushie.armourers_workshop.api.common.skin.data;

import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import net.minecraft.util.EnumFacing;

public interface ISkinPart {

    public ISkinPartType getPartType();
    
    public int getMarkerCount();
    
    public Point3D getMarker(int index);
    
    public EnumFacing getMarkerSide(int index);
}
