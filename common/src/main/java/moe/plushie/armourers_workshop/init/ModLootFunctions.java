package moe.plushie.armourers_workshop.init;

import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.api.common.ILootFunction;
import moe.plushie.armourers_workshop.api.common.ILootFunctionType;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.init.function.SkinRandomlyFunction;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;

@SuppressWarnings("unused")
public class ModLootFunctions {

    public static final IRegistryKey<ILootFunctionType<SkinRandomlyFunction>> SKIN_RANDOMLY = of(SkinRandomlyFunction.CODEC).build("skin_randomly");

    public static void init() {
    }

    private static <T extends ILootFunction> ILootFunctionBuilder<T> of(Codec<? extends T> codec) {
        return BuilderManager.getInstance().createLootFunctionBuilder(codec);
    }
}
