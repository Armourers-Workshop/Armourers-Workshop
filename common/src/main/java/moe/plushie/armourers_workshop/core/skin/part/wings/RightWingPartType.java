package moe.plushie.armourers_workshop.core.skin.part.wings;

import moe.plushie.armourers_workshop.api.skin.part.features.ICanRotation;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class RightWingPartType extends SkinPartType implements ICanRotation {

    public RightWingPartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(0, -27, -28, 48, 64, 64);
        this.guideSpace = new OpenRectangle3i(-4, -12, -4, 8, 12, 4);
        this.offset = new OpenVector3i(0, -1, 2);
        this.renderOffset = new OpenVector3i(0, 0, 2);
        this.renderPolygonOffset = 1;
    }

    @Override
    public int maximumMarkersNeeded() {
        return 1;
    }

    @Override
    public int minimumMarkersNeeded() {
        return 1;
    }
}
