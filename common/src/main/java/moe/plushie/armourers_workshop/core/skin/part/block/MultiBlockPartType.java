package moe.plushie.armourers_workshop.core.skin.part.block;

import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

public class MultiBlockPartType extends BlockPartType {

    public MultiBlockPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-24, -8, -8, 48, 48, 48);
        this.guideSpace = new Rectangle3i(0, 0, 0, 0, 0, 0);
        this.offset = new Vector3i(0, -1, 0);
    }
}
