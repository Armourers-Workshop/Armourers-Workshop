package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ILootItemFunctionType;
import moe.plushie.armourers_workshop.compat.core.AbstractLootItemFunctionType;
import moe.plushie.armourers_workshop.compat.fabric.AbstractFabricRegistry;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[16, )")
@Extension
public class LootFunctionTypeExt {

    public static TypedProvider<ILootItemFunctionType<?>> createLootItemFunctionTypeRegistryFA(@ThisClass Class<?> clazz) {
        return AbstractFabricRegistry.from(BuiltInRegistriesExt.LOOT_FUNCTION_TYPE).map(AbstractLootItemFunctionType::unwrap);
    }
}
