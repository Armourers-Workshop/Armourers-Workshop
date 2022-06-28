package moe.plushie.armourers_workshop.core.skin.part.wings;

import moe.plushie.armourers_workshop.api.action.ICanRotation;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraft.util.math.vector.Vector3i;

public class RightWingPartType extends SkinPartType implements ICanRotation {

    public RightWingPartType() {
        super();
        this.buildingSpace = new Rectangle3i(0, -32, -28, 48, 64, 64);
        this.guideSpace = new Rectangle3i(-4, -12, -4, 8, 12, 4);
        this.offset = new Vector3i(0, -1, 2);
    }

    @Override
    public Vector3i getRenderOffset() {
        return new Vector3i(0, 0, 2);
    }

    @Override
    public float getRenderPolygonOffset() {
        return 1;
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
