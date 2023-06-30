package extensions.net.minecraft.client.Options;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.CameraType;
import net.minecraft.client.Options;

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
