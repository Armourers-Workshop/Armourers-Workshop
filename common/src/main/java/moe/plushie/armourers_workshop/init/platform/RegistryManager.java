package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public class RegistryManager {

    @ExpectPlatform
    public static <T> ResourceLocation getKey(T value) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T> Collection<IRegistryKey<T>> getEntries(Class<T> clazz) {
        throw new AssertionError();
    }
}
