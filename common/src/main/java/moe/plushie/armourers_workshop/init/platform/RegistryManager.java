package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.core.registry.Registry;

public class RegistryManager {

    @ExpectPlatform
    public static <T> Registry<T> makeRegistry(Class<? super T> clazz) {
        throw new AssertionError();
    }
}
