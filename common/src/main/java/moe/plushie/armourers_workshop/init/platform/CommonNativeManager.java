package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.init.provider.CommonNativeFactory;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;

public class CommonNativeManager {

    @ExpectPlatform
    public static CommonNativeFactory getFactory() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static CommonNativeProvider getProvider() {
        throw new AssertionError();
    }
}
