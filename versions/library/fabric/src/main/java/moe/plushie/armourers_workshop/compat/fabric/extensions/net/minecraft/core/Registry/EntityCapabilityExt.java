package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[16, )")
@Extension
public class EntityCapabilityExt {

    public static TypedProvider<IEntityCapability<?>> createEntityCapabilityRegistryFA(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough();
    }

    public static TypedProvider<IBlockEntityCapability<?>> createBlockEntityCapabilityRegistryFA(@ThisClass Class<?> clazz) {
        return TypedProvider.passthrough();
    }
}
