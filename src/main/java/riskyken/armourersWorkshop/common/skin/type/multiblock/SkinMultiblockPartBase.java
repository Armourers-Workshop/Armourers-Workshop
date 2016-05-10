package riskyken.armourersWorkshop.common.skin.type.multiblock;

import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinMultiblockPartBase extends AbstractSkinPartTypeBase {

    private String partName;
    
    public SkinMultiblockPartBase(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-8, -8, -8, 16, 16, 16);
        this.guideSpace = new Rectangle3D(0, 0, 0, 0, 0, 0);
        this.offset = new Point3D(0, -1, 0);
    }
    
    public SkinMultiblockPartBase setPartName(String partName) {
        this.partName = partName;
        return this;
    }
    
    public SkinMultiblockPartBase setOffset(Point3D offset) {
        this.offset = offset;
        return this;
    }
    

    @Override
    public String getPartName() {
        return partName;
    }

    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
    }
}
