package moe.plushie.armourers_workshop.core.skin.part.horse;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Point3D;
import moe.plushie.armourers_workshop.core.utils.Rectangle3D;

public class SkinHorsePartBase extends SkinPartType {

    public SkinHorsePartBase() {
        super();
        this.buildingSpace = new Rectangle3D(-32, -32, -32, 64, 64, 64);
        this.guideSpace = new Rectangle3D(-5, -8, -19, 10, 10, 24);
        this.offset = new Point3D(0, 0, 0);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
    }

    @Override
    public int getPolygonOffset() {
        return 8;
    }
}
