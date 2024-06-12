package moe.plushie.armourers_workshop.init;

import com.mojang.serialization.MapCodec;
import moe.plushie.armourers_workshop.api.common.ILootFunction;
import moe.plushie.armourers_workshop.api.common.ILootFunctionType;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.init.function.SkinRandomlyFunction;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;

@SuppressWarnings("unused")
public class ModLootFunctions {

    public static final IRegistryHolder<ILootFunctionType<SkinRandomlyFunction>> SKIN_RANDOMLY = of(SkinRandomlyFunction.MAP_CODEC).build("skin_randomly");

    public static void init() {
    }

    private static <T extends ILootFunction> ILootFunctionTypeBuilder<T> of(MapCodec<T> codec) {
        return BuilderManager.getInstance().createLootFunctionTypeBuilder(codec);
    }
}
