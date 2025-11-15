package moe.plushie.armourers_workshop.compat.core;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ILootItemFunction;
import moe.plushie.armourers_workshop.api.common.ILootItemFunctionType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Available("[1.21, 1.22)")
public class AbstractLootItemFunctionType<T extends ILootItemFunction> implements ILootItemFunctionType<T> {

    private final LootItemFunctionType<?> type;

    public AbstractLootItemFunctionType(LootItemFunctionType<?> type) {
        this.type = type;
    }

    public static <T extends ILootItemFunction> AbstractLootItemFunctionType<T> conditional(IDataMapCodec<T> codec) {
        LootItemFunctionType<?>[] type = {null};
        type[0] = new LootItemFunctionType<>(ConditionalFunction.createCodec(() -> Objects.unsafeCast(type[0]), codec.mapCodec()));
        return new AbstractLootItemFunctionType<>(type[0]);
    }

    public static LootItemFunctionType<?> unwrap(ILootItemFunctionType<?> type) {
        return ((AbstractLootItemFunctionType<?>) type).type;
    }

    public static class ConditionalFunction<T extends ILootItemFunction> extends LootItemConditionalFunction {

        private final T value;
        private final Supplier<LootItemFunctionType<? extends LootItemConditionalFunction>> type;

        protected ConditionalFunction(List<LootItemCondition> args, T value, Supplier<LootItemFunctionType<? extends LootItemConditionalFunction>> type) {
            super(args);
            this.type = type;
            this.value = value;
        }

        public static <T extends ILootItemFunction> MapCodec<? extends ConditionalFunction<T>> createCodec(Supplier<LootItemFunctionType<? extends LootItemConditionalFunction>> type, MapCodec<T> codec) {
            return RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
                    .and(codec.forGetter(ConditionalFunction::getValue))
                    .apply(instance, (args, value) -> new ConditionalFunction<>(args, value, type)));
        }

        @Override
        protected ItemStack run(ItemStack arg, LootContext arg2) {
            return value.apply(arg, AbstractLootContext.wrap(arg2));
        }

        @Override
        public Set<LootContextParam<?>> getReferencedContextParams() {
            return Collections.newSet(Collections.compactMap(value.getReferencedContextParams(), AbstractLootContextParams::unwrap));
        }

        @Override
        public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
            return type.get();
        }

        public T getValue() {
            return value;
        }
    }
}
