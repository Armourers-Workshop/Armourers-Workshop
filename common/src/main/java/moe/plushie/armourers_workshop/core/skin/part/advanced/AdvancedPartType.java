package moe.plushie.armourers_workshop.core.skin.part.advanced;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class AdvancedPartType extends SkinPartType {

    public AdvancedPartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(-32, -32, -32, 64, 64, 64);
        this.guideSpace = new OpenRectangle3i(0, 0, 0, 0, 0, 0);
        this.offset = new OpenVector3i(0, 0, 0);
    }

    @Override
    public int minimumMarkersNeeded() {
        return 0;
    }

    @Override
    public int maximumMarkersNeeded() {
        return 0;
    }
}
