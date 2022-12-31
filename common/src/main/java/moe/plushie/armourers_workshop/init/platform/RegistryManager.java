package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.common.IRegistry;

public class RegistryManager {

    @ExpectPlatform
    public static <T> IRegistry<T> makeRegistry(Class<? super T> clazz) {
        throw new AssertionError();
    }
}
