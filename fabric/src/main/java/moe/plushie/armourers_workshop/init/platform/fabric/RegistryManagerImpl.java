package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricRegistry;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;

public class RegistryManagerImpl {

    public static <T> ResourceLocation getKey(T value) {
        for (AbstractFabricRegistry<?> registry : AbstractFabricRegistry.INSTANCES) {
            if (registry.getType().isInstance(value)) {
                AbstractFabricRegistry<T> registry1 = ObjectUtils.unsafeCast(registry);
                return registry1.getKey(value);
            }
        }
        return new ResourceLocation("air");
    }

    public static <T> Collection<IRegistryKey<? extends T>> getEntries(Class<T> clazz) {
        for (AbstractFabricRegistry<?> registry : AbstractFabricRegistry.INSTANCES) {
            if (clazz.isAssignableFrom(registry.getType())) {
                AbstractFabricRegistry<T> registry1 = ObjectUtils.unsafeCast(registry);
                return registry1.getEntries();
            }
        }
        return Collections.emptyList();
    }
}
