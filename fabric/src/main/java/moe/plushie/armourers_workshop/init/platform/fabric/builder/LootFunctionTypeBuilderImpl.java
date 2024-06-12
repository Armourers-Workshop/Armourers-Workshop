package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import com.mojang.serialization.MapCodec;
import moe.plushie.armourers_workshop.api.common.ILootFunction;
import moe.plushie.armourers_workshop.api.common.ILootFunctionType;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.compatibility.core.AbstractLootItemFunctionType;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricRegistries;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.TypedRegistry;

public class LootFunctionTypeBuilderImpl<T extends ILootFunction> implements ILootFunctionTypeBuilder<T> {

    private final MapCodec<T> codec;

    public LootFunctionTypeBuilderImpl(MapCodec<T> codec) {
        this.codec = codec;
    }

    @Override
    public IRegistryHolder<ILootFunctionType<T>> build(String name) {
        AbstractLootItemFunctionType<T> proxy = AbstractLootItemFunctionType.conditional(codec);
        AbstractFabricRegistries.ITEM_LOOT_FUNCTIONS.register(name, proxy::getType);
        return TypedRegistry.Entry.of(ModConstants.key(name), () -> proxy);
    }
}
