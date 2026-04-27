package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.client.ICamera;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3d;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

public class AbstractCamera extends AbstractCameraImpl implements ICamera {

    protected final Camera camera;

    protected AbstractCamera(Camera camera) {
        this.camera = camera;
    }

    public static AbstractCamera wrap(Camera camera) {
        return new AbstractCamera(camera);
    }

    public static AbstractCamera getMainCamera() {
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        return wrap(camera);
    }

    public OpenQuaternionf lookAt(OpenVector3d pos) {
        var forward = new OpenVector3f(position().subtracting(pos));
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

    @Override
    public OpenVector3d position() {
        var pos = getPosition();
        return new OpenVector3d(pos.x(), pos.y(), pos.z());
    }

    @Override
    public OpenQuaternionf rotation() {
        return OpenQuaternionf.fromEulerAnglesYXZ(180 - getYRot(), -getXRot(), 0, true);
    }

    @Override
    public OpenQuaternionf orientation() {
//        var quat = minecraft.getEntityRenderDispatcher().cameraOrientation();
//        return new OpenQuaternionf(quat.x, quat.y, quat.z, quat.w);

//        var quat = minecraft.getEntityRenderDispatcher().cameraOrientation();
//        return new OpenQuaternionf(quat.i(), quat.j(), quat.k(), quat.r());

        //        var quat = minecraft.getEntityRenderDispatcher().cameraOrientation();

        return OpenQuaternionf.ONE;
    }

    @Override
    protected Camera getCamera() {
        return camera;
    }
}
