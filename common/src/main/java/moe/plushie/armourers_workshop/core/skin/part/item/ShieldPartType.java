package moe.plushie.armourers_workshop.core.skin.part.item;

import moe.plushie.armourers_workshop.api.action.ICanHeld;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

public class ShieldPartType extends SkinPartType implements ICanHeld {

    public ShieldPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-32, -36, -32, 64, 72, 64);
        this.guideSpace = new Rectangle3i(-2, -2, 2, 4, 4, 8);
        //Offset -1 to match old skin system.
        this.offset = new Vector3i(0, -1, 0);
        this.renderOffset = Vector3i.ZERO;
        this.renderPolygonOffset = 10;
    }
}
