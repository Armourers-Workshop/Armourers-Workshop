package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IDataComponentType;
import moe.plushie.armourers_workshop.compat.core.data.AbstractDataComponentType;
import moe.plushie.armourers_workshop.compat.fabric.core.AbstractFabricRegistry;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[21, )")
@Extension
public class DataComponentTypeExt {

    public static TypedProvider<IDataComponentType<?>> createDataComponentTypeRegistryFA(@ThisClass Class<?> clazz) {
        return AbstractFabricRegistry.from(BuiltInRegistriesExt.DATA_COMPONENT_TYPE).map(AbstractDataComponentType::unwrap);
    }
}
