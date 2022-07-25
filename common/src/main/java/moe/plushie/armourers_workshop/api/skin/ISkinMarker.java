package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.core.Direction;

public interface ISkinMarker {

    Vector3i getPosition();

    Direction getDirection();
}
