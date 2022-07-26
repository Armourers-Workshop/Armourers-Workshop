package moe.plushie.armourers_workshop.api.other;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface IRegistryObject<T> extends Supplier<T> {

    /**
     * A unique identifier for this entry, if this entry is registered already it will return it's official registry name.
     * Otherwise it will return the name set in setRegistryName().
     * If neither are valid null is returned.
     *
     * @return Unique identifier or null.
     */
    ResourceLocation getRegistryName();
}
