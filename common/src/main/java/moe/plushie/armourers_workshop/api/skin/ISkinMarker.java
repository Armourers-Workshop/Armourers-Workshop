package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.api.core.math.IVector3i;
import moe.plushie.armourers_workshop.api.core.utils.IDirection;

public interface ISkinMarker {

    IVector3i position();

    IDirection direction();
}
