package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.client.Minecraft;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import net.minecraft.client.Minecraft;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.20, 1.26)")
@Extension
public class CameraExt {

    public static OpenVector3f getCameraPosition(@This Minecraft minecraft) {
        var pos = minecraft.getEntityRenderDispatcher().camera.getPosition();
        return new OpenVector3f(pos.x, pos.y, pos.z);
    }

    public static OpenQuaternionf getCameraOrientation(@This Minecraft minecraft) {
        var quat = minecraft.getEntityRenderDispatcher().cameraOrientation();
        return new OpenQuaternionf(quat.x, quat.y, quat.z, quat.w);
    }
}
