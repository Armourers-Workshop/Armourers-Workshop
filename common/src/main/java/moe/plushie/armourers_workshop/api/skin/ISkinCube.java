package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import net.minecraft.core.Direction;

public interface ISkinCube {

    IVector3i getPos();

    ISkinCubeType getType();

    IPaintColor getPaintColor(Direction dir);
}
