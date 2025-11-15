package moe.plushie.armourers_workshop.compat.forge.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeRegistry;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.16, )")
@Extension
public class EntityTypeExt {

    public static TypedProvider<IEntityType<?>> createEntityTypeRegistryFO(@ThisClass Class<?> clazz) {
        return AbstractForgeRegistry.from(BuiltInRegistriesExt.ENTITY_TYPE).map(Supplier::get);
    }

    public static TypedProvider<IBlockEntityType<?>> createBlockEntityTypeRegistryFO(@ThisClass Class<?> clazz) {
        return AbstractForgeRegistry.from(BuiltInRegistriesExt.BLOCK_ENTITY_TYPE).map(Supplier::get);
    }
}
