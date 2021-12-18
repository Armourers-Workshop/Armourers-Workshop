package moe.plushie.armourers_workshop.core.skin.part.block;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Point3D;
import moe.plushie.armourers_workshop.core.utils.Rectangle3D;

public class SkinBlockPartBase extends SkinPartType {

    public SkinBlockPartBase() {
        super();
        this.buildingSpace = new Rectangle3D(-8, -8, -8, 16, 16, 16);
        this.guideSpace = new Rectangle3D(0, 0, 0, 0, 0, 0);
        this.offset = new Point3D(0, -1, 0);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
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
