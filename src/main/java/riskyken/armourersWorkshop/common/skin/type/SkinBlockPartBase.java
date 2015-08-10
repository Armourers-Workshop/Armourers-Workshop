package riskyken.armourersWorkshop.common.skin.type;

import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;

public class SkinBlockPartBase extends AbstractSkinPartTypeBase {

    public SkinBlockPartBase(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-8, 0, -8, 16, 16, 16);
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
