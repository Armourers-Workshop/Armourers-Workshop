package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.fabric.core.AbstractFabricRegistry;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import net.minecraft.world.item.CreativeModeTab;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[20, )")
@Extension
public class CreativeModeTabExt {

    public static TypedProvider<CreativeModeTab> createCreativeModeTabRegistryFA(@ThisClass Class<?> clazz) {
        return AbstractFabricRegistry.from(BuiltInRegistriesExt.CREATIVE_MODE_TAB);
    }
}
