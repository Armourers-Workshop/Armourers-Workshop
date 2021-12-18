package moe.plushie.armourers_workshop.core.api.common.skin;

import moe.plushie.armourers_workshop.core.skin.type.Point3D;
import net.minecraft.util.Direction;

public interface ISkinMarker {

    Point3D getPosition();

    Direction getDirection();
}
