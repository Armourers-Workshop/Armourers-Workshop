package moe.plushie.armourers_workshop.core.skin.particle.runtime;

import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle2f;
import moe.plushie.armourers_workshop.core.math.OpenSize2f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.particle.math.ParticleCameraFacing;

public interface GraphicsAccessor {

    void submit(OpenVector3f position, OpenQuaternionf rotation, OpenSize2f size, OpenRectangle2f textureBox, ParticleCameraFacing cameraFacing);
}
