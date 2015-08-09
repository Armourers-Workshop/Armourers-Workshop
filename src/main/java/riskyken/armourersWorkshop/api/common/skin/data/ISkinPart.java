package riskyken.armourersWorkshop.api.common.skin.data;

import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;

public interface ISkinPart {

    public ISkinPartType getPartType();
    
    public int getMarkerCount();
    
    public Point3D getMarker(int index);
    
    public ForgeDirection getMarkerSide(int index);
}
