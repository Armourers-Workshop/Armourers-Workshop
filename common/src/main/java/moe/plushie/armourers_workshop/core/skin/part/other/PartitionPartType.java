package moe.plushie.armourers_workshop.core.skin.part.other;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class PartitionPartType extends SkinPartType {

    public final SkinPartType parentPartType;

    public PartitionPartType(SkinPartType parentPartType) {
        super();
        this.buildingSpace = new OpenRectangle3i(-32, -32, -32, 64, 64, 64);
        this.guideSpace = OpenRectangle3i.ZERO;
        this.offset = OpenVector3i.ZERO;
        this.renderOffset = new OpenVector3i(parentPartType.renderOffset());
        this.renderPolygonOffset = parentPartType.renderPolygonOffset();
        this.parentPartType = parentPartType;
    }
}
