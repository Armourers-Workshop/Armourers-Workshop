package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.math.IQuaternionf;
import moe.plushie.armourers_workshop.api.core.math.IVector3d;

public interface ICamera {

    IVector3d position();

    IQuaternionf rotation();

    IQuaternionf orientation();
}
