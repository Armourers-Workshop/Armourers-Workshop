package moe.plushie.armourers_workshop.init.platform.forge.builder;

import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.api.common.ILootFunction;
import moe.plushie.armourers_workshop.api.common.ILootFunctionType;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.core.AbstractLootItemFunction;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.TypedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import java.util.function.Supplier;

public class LootFunctionBuilderImpl<T extends ILootFunction> implements ILootFunctionBuilder<T> {

    private final Codec<? extends T> codec;

    public LootFunctionBuilderImpl(Codec<? extends T> codec) {
        this.codec = codec;
    }

    @Override
    public IRegistryKey<ILootFunctionType<T>> build(String name) {
        LootItemFunctionType[] type = {null};
        ResourceLocation registryName = ModConstants.key(name);
        type[0] = createType(() -> type[0]);
        Proxy<T> proxy = new Proxy<>(type[0]);
        AbstractForgeRegistries.ITEM_LOOT_FUNCTIONS.register(name, () -> type[0]);
        return TypedRegistry.Entry.of(registryName, () -> proxy);
    }

    public LootItemFunctionType createType(Supplier<LootItemFunctionType> type) {
        return new LootItemFunctionType(AbstractLootItemFunction.conditional(type, codec));
    }

    public static class Proxy<T extends ILootFunction> implements ILootFunctionType<T> {

        private final LootItemFunctionType type;

        public Proxy(LootItemFunctionType type) {
            this.type = type;
        }
    }
}
