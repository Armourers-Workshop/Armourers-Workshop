package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import net.minecraft.client.Camera;

public class AbstractCamera {

    private final Camera camera;

    private AbstractCamera(Camera camera) {
        this.camera = camera;
    }

    public static AbstractCamera wrap(Camera camera) {
        return new AbstractCamera(camera);
    }

    public OpenQuaternionf lookAt(OpenVector3f pos) {
        var forward = position().subtracting(pos);
        var length = forward.dot(forward);
        if (length < 1e-6f) {
            return OpenQuaternionf.identity();
        }
        forward.scale(OpenMath.invsqrt(length));
        var dir = new OpenVector3f(0, 1, 0);
        var right = dir.crossing(forward).normalizing();
        var up = forward.crossing(right);
        return OpenQuaternionf.fromUnnormalizedMatrix(right.x, right.y, right.z, up.x, up.y, up.z, forward.x, forward.y, forward.z);
    }

    public OpenVector3f position() {
        var pos = camera.getPosition();
        return new OpenVector3f(pos.x(), pos.y(), pos.z());
    }

    public OpenQuaternionf rotation() {
        return OpenQuaternionf.fromEulerAnglesYXZ(180 - camera.getYRot(), -camera.getXRot(), 0, true);
    }
}
