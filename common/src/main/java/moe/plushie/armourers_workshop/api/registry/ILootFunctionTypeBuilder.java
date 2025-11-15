package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.common.ILootItemFunction;
import moe.plushie.armourers_workshop.api.common.ILootItemFunctionType;

@SuppressWarnings("unused")
public interface ILootFunctionTypeBuilder<T extends ILootItemFunction> extends IRegistryBuilder<ILootItemFunctionType<T>> {
}
