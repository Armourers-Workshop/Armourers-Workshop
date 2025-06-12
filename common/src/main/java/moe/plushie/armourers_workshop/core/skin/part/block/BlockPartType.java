package moe.plushie.armourers_workshop.core.skin.part.block;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class BlockPartType extends SkinPartType {

    public BlockPartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(-8, -8, -8, 16, 16, 16);
        this.guideSpace = new OpenRectangle3i(0, 0, 0, 0, 0, 0);
        this.offset = new OpenVector3i(0, -1, 0);
    }

    @Override
    public int maximumMarkersNeeded() {
        return 1;
    }

    @Override
    public int minimumMarkersNeeded() {
        return 0;
    }
}
