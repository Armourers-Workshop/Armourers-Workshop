package riskyken.armourersWorkshop.common.skin.type.block;

import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinBlockPartMultiBlock extends AbstractSkinPartTypeBase {
    
    public SkinBlockPartMultiBlock(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-24, -8, -8, 48, 48, 48);
        this.guideSpace = new Rectangle3D(0, 0, 0, 0, 0, 0);
        this.offset = new Point3D(0, -1, 0);
    }

    @Override
    public String getPartName() {
        return "multiblock";
    }

    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
    }
    
    @Override
    public int getMaximumMarkersNeeded() {
        return 1;
    }
    
    @Override
    public int getMinimumMarkersNeeded() {
        return 0;
    }
}
