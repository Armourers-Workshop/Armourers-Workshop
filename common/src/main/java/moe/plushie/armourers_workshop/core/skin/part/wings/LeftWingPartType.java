package moe.plushie.armourers_workshop.core.skin.part.wings;

import moe.plushie.armourers_workshop.api.action.ICanRotation;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

public class LeftWingPartType extends SkinPartType implements ICanRotation {

    public LeftWingPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-48, -27, -28, 48, 64, 64);
        this.guideSpace = new Rectangle3i(-4, -12, -4, 8, 12, 4);
        this.offset = new Vector3i(0, -1, 2);
        this.renderOffset = new Vector3i(0, 0, 2);
        this.renderPolygonOffset = 1;
    }

    @Override
    public boolean isMirror() {
        return true;
    }

    @Override
    public int getMaximumMarkersNeeded() {
        return 1;
    }

    @Override
    public int getMinimumMarkersNeeded() {
        return 1;
    }
}
