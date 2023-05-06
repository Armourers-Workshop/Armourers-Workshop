package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistry;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;

public class RegistryManagerImpl {

    public static <T> ResourceLocation getKey(T value) {
        for (AbstractForgeRegistry<?> registry : AbstractForgeRegistry.INSTANCES) {
            if (registry.getType().isInstance(value)) {
                AbstractForgeRegistry<T> registry1 = ObjectUtils.unsafeCast(registry);
                return registry1.getKey(value);
            }
        }
        return new ResourceLocation("air");
    }

    public static <T> Collection<IRegistryKey<? extends T>> getEntries(Class<T> clazz) {
        for (AbstractForgeRegistry<?> registry : AbstractForgeRegistry.INSTANCES) {
            if (clazz.isAssignableFrom(registry.getType())) {
                AbstractForgeRegistry<T> registry1 = ObjectUtils.unsafeCast(registry);
                return registry1.getEntries();
            }
        }
        return Collections.emptyList();
    }
}
