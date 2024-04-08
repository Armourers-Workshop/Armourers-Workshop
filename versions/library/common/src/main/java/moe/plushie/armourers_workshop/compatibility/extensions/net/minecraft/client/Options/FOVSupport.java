package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.Options;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import net.minecraft.client.CameraType;
import net.minecraft.client.Options;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.19, )")
public class FOVSupport {

    public static float getCameraFOV(@This Options options) {
        return options.fov().get().floatValue();
    }

    public static float getCameraNear(@This Options options) {
        if (options.getCameraType() == CameraType.FIRST_PERSON) {
            return 0.05f;
        }
        return 4.0f;
    }
}
