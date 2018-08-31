package riskyken.armourers_workshop.api.common.skin.data;

import net.minecraft.util.EnumFacing;
import riskyken.armourers_workshop.api.common.skin.Point3D;
import riskyken.armourers_workshop.api.common.skin.type.ISkinPartType;

public interface ISkinPart {

    public ISkinPartType getPartType();
    
    public int getMarkerCount();
    
    public Point3D getMarker(int index);
    
    public EnumFacing getMarkerSide(int index);
}
