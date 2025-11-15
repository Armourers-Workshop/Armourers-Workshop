package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.compat.fabric.AbstractFabricRegistry;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.16, )")
@Extension
public class EntityTypeExt {

    public static TypedProvider<IEntityType<?>> createEntityTypeRegistryFA(@ThisClass Class<?> clazz) {
        return AbstractFabricRegistry.from(BuiltInRegistriesExt.ENTITY_TYPE).map(Supplier::get);
    }

    public static TypedProvider<IBlockEntityType<?>> createBlockEntityTypeRegistryFA(@ThisClass Class<?> clazz) {
        return AbstractFabricRegistry.from(BuiltInRegistriesExt.BLOCK_ENTITY_TYPE).map(Supplier::get);
    }
}
