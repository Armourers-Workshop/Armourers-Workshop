package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.ILootItemFunction;
import moe.plushie.armourers_workshop.api.common.ILootItemFunctionType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionTypeBuilder;
import moe.plushie.armourers_workshop.init.function.SkinRandomlyFunction;
import moe.plushie.armourers_workshop.init.platform.Platform;

@SuppressWarnings("unused")
public class ModLootFunctionTypes {

    public static final IRegistryHolder<ILootItemFunctionType<SkinRandomlyFunction>> SKIN_RANDOMLY = normal(SkinRandomlyFunction.MAP_CODEC).build("skin_randomly");

    private static <T extends ILootItemFunction> ILootFunctionTypeBuilder<T> normal(IDataMapCodec<T> codec) {
        return Platform.get().common().builder().lootFunctionType(codec);
    }

    public static void init() {
    }
}
