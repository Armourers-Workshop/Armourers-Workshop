package moe.plushie.armourers_workshop.api.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface IRegistryKey<T> extends Supplier<T> {

    /**
     * A unique identifier for this entry, if this entry is registered already it will return it's official registry name.
     * If neither are valid null is returned.
     *
     * @return Unique identifier or null.
     */
    ResourceLocation getRegistryName();
}
