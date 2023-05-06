package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.ILootFunction;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionBuilder;

import java.util.function.Supplier;

public class LootFunctionBuilderImpl<T extends ILootFunction> implements ILootFunctionBuilder<T> {

    private final Supplier<ILootFunction.Serializer<T>> serializer;

    public LootFunctionBuilderImpl(Supplier<ILootFunction.Serializer<T>> serializer) {
        this.serializer = serializer;
//        d.register(MinecraftForge.EVENT_BUS);
    }

//    DeferredRegister<LootItemFunctionType> d = DeferredRegister.create(ForgeRegistry.LOOT_FUNCTION_REGISTRY, ModConstants.MOD_ID);


    @Override
    public IRegistryKey<T> build(String name) {
        ILootFunction.Serializer<T> serializer1 = serializer.get();
//        LootItemFunctionType type = new LootItemFunctionType(new Serializer<T>() {
//
//
//
//        });
//        d.register(name, () -> type);
//        return type;
        return null;
    }
}
