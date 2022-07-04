package moe.plushie.armourers_workshop.core.skin.part.bow;

import moe.plushie.armourers_workshop.api.action.ICanHeld;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraft.util.math.vector.Vector3i;

public class ArrowPartType extends SkinPartType implements ICanHeld {

    public ArrowPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-8, -8, -6, 16, 16, 24);
        this.guideSpace = new Rectangle3i(0, 0, 0, 0, 0, 0);
        this.offset = new Vector3i(0, 0, 25);
    }
}
