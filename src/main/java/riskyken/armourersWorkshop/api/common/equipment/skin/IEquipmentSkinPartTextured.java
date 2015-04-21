package riskyken.armourersWorkshop.api.common.equipment.skin;

import java.awt.Point;

import javax.vecmath.Point3i;

public interface IEquipmentSkinPartTextured extends IEquipmentSkinPart {

    public Point getTextureLocation();
    
    public boolean isTextureMirrored();
    
    public Point3i getTextureModelSize();
}
