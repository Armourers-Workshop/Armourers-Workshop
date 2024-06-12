package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.Minecraft;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import net.minecraft.client.Minecraft;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.20, )")
public class Camera {

    public static OpenQuaternionf getCameraOrientation(@This Minecraft minecraft) {
        var quat = minecraft.getEntityRenderDispatcher().cameraOrientation();
        return new OpenQuaternionf(quat.x, quat.y, quat.z, quat.w);
    }
}
