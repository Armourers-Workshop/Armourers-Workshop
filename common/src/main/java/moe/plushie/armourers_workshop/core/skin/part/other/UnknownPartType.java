package moe.plushie.armourers_workshop.core.skin.part.other;

import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

public class UnknownPartType extends SkinPartType {

    public UnknownPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-32, -32, -32, 64, 64, 64);
        this.guideSpace = new Rectangle3i(0, 0, 0, 0, 0, 0);
        this.offset = new Vector3i(0, 0, 0);
    }
}
