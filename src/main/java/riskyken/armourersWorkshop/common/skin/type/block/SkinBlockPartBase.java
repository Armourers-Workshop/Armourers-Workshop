package riskyken.armourersWorkshop.common.skin.type.block;

import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinBlockPartBase extends AbstractSkinPartTypeBase {

    public SkinBlockPartBase(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-24, -8, -8, 48, 48, 48);
        this.guideSpace = new Rectangle3D(0, 0, 0, 0, 0, 0);
        this.offset = new Point3D(0, -1, 0);
    }

    @Override
    public String getPartName() {
        return "base";
    }

    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
    }
}
