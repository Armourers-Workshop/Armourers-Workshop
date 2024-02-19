package moe.plushie.armourers_workshop.compatibility.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ILootFunction;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Available("[1.21, )")
public class AbstractLootItemFunction {

    public static <T extends ILootFunction> Codec<? extends LootItemFunction> conditional(Supplier<LootItemFunctionType> type, Codec<T> codec) {
        MapCodec.MapCodecCodec<T> mapCodec = ObjectUtils.unsafeCast(codec);
        return ConditionalFunction.createCodec(type, mapCodec.codec());
    }

    public static class ConditionalFunction<T extends ILootFunction> extends LootItemConditionalFunction {

        private final T value;
        private final Supplier<LootItemFunctionType> type;

        protected ConditionalFunction(List<LootItemCondition> args, T value, Supplier<LootItemFunctionType> type) {
            super(args);
            this.type = type;
            this.value = value;
        }

        public static <T extends ILootFunction> Codec<? extends ConditionalFunction<T>> createCodec(Supplier<LootItemFunctionType> type, MapCodec<T> codec) {
            return RecordCodecBuilder.create((instance) -> commonFields(instance)
                    .and(codec.forGetter(ConditionalFunction::getValue))
                    .apply(instance, (args, value) -> new ConditionalFunction<>(args, value, type)));
        }

        @Override
        protected ItemStack run(ItemStack arg, LootContext arg2) {
            return value.apply(arg, arg2);
        }

        @Override
        public void validate(ValidationContext arg) {
            value.validate(arg);
        }

        @Override
        public Set<LootContextParam<?>> getReferencedContextParams() {
            return value.getReferencedContextParams();
        }

        @Override
        public LootItemFunctionType getType() {
            return type.get();
        }

        public T getValue() {
            return value;
        }
    }
}
