package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.core.data.DataPackLoader;

public class DataPackManager {

    @ExpectPlatform
    public static void register(DataPackLoader loader) {
        throw new AssertionError();
    }
}
