package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.api.math.IVector3i;
import net.minecraft.core.Direction;

public interface ISkinMarker {

    IVector3i getPosition();

    Direction getDirection();
}
