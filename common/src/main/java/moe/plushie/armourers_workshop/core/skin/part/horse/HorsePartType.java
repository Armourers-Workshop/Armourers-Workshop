package moe.plushie.armourers_workshop.core.skin.part.horse;

import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

public class HorsePartType extends SkinPartType {

    public HorsePartType() {
        super();
        this.buildingSpace = new Rectangle3i(-32, -32, -32, 64, 64, 64);
        this.guideSpace = new Rectangle3i(-5, -8, -19, 10, 10, 24);
        this.offset = new Vector3i(0, 0, 0);
    }
}
