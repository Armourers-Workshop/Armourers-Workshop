package riskyken.armourersWorkshop.api.common.skin.type;

import java.awt.Point;

import javax.vecmath.Point3i;

public interface ISkinPartTypeTextured extends ISkinPartType {

    public Point getTextureLocation();
    
    public boolean isTextureMirrored();
    
    public Point3i getTextureModelSize();
}
