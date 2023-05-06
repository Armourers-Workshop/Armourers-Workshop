package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.ILootFunction;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionBuilder;
import moe.plushie.armourers_workshop.init.function.SkinRandomlyFunction;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModLootFunctions {

    public static final IRegistryKey<SkinRandomlyFunction> SKIN_RANDOMLY = of(SkinRandomlyFunction.Serializer::new).build("skin_randomly");

    public static void init() {
    }

    private static <T extends ILootFunction> ILootFunctionBuilder<T> of(Supplier<ILootFunction.Serializer<T>> serializer) {
        return BuilderManager.getInstance().createLootFunctionBuilder(serializer);
    }
}
