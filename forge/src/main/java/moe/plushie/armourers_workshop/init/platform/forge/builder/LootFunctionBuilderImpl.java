package moe.plushie.armourers_workshop.init.platform.forge.builder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import moe.plushie.armourers_workshop.api.common.ILootConditionalFunction;
import moe.plushie.armourers_workshop.api.common.ILootFunction;
import moe.plushie.armourers_workshop.api.common.ILootFunctionType;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistryEntry;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Set;
import java.util.function.Supplier;

public class LootFunctionBuilderImpl<T extends ILootFunction> implements ILootFunctionBuilder<T> {

    private final Supplier<ILootFunction.Serializer<T>> serializer;

    public LootFunctionBuilderImpl(Supplier<ILootFunction.Serializer<T>> serializer) {
        this.serializer = serializer;
    }

    @Override
    public IRegistryKey<ILootFunctionType<T>> build(String name) {
        LootItemFunctionType[] type = {null};
        ResourceLocation registryName = ModConstants.key(name);
        type[0] = createType(() -> type[0], serializer.get());
        Proxy<T> proxy = new Proxy<>(type[0]);
        Registry.registerItemLootFunctionFO(name, () -> type[0]);
        return new AbstractForgeRegistryEntry<>(registryName, () -> proxy);
    }

    public LootItemFunctionType createType(Supplier<LootItemFunctionType> type, ILootFunction.Serializer<T> serializer) {
        if (serializer instanceof ILootConditionalFunction.Serializer) {
            ILootConditionalFunction.Serializer<?> serializer2 = ObjectUtils.unsafeCast(serializer);
            return new LootItemFunctionType(new ConditionalFunction.Factory<>(type, serializer2));
        }
        return new LootItemFunctionType(new NormalFunction.Factory<>(type, serializer));
    }

    public static class Proxy<T extends ILootFunction> implements ILootFunctionType<T> {

        private final LootItemFunctionType type;

        public Proxy(LootItemFunctionType type) {
            this.type = type;
        }
    }

    public static class NormalFunction<T extends ILootFunction> implements LootItemFunction {

        private final T value;
        private final Supplier<LootItemFunctionType> type;

        public NormalFunction(T value, Supplier<LootItemFunctionType> type) {
            this.type = type;
            this.value = value;
        }

        @Override
        public LootItemFunctionType getType() {
            return type.get();
        }

        @Override
        public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
            return value.apply(itemStack, lootContext);
        }

        @Override
        public void validate(ValidationContext arg) {
            value.validate(arg);
        }

        @Override
        public Set<LootContextParam<?>> getReferencedContextParams() {
            return value.getReferencedContextParams();
        }

        public static class Factory<T extends ILootFunction> implements Serializer<NormalFunction<T>> {

            private final Supplier<LootItemFunctionType> type;
            private final ILootFunction.Serializer<T> serializer;

            public Factory(Supplier<LootItemFunctionType> type, ILootFunction.Serializer<T> serializer) {
                this.type = type;
                this.serializer = serializer;
            }

            @Override
            public void serialize(JsonObject object, NormalFunction<T> arg, JsonSerializationContext context) {
                serializer.serialize(IDataPackObject.of(object), arg.value);
            }

            @Override
            public NormalFunction<T> deserialize(JsonObject object, JsonDeserializationContext context) {
                T value = serializer.deserialize(IDataPackObject.of(object));
                return new NormalFunction<>(value, type);
            }
        }
    }

    public static class ConditionalFunction<T extends ILootConditionalFunction> extends LootItemConditionalFunction {

        private final T value;
        private final Supplier<LootItemFunctionType> type;

        protected ConditionalFunction(T value, LootItemCondition[] args, Supplier<LootItemFunctionType> type) {
            super(args);
            this.type = type;
            this.value = value;
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

        public static class Factory<T extends ILootConditionalFunction> extends LootItemConditionalFunction.Serializer<ConditionalFunction<T>> {

            private final Supplier<LootItemFunctionType> type;
            private final ILootConditionalFunction.Serializer<T> serializer;

            public Factory(Supplier<LootItemFunctionType> type, ILootConditionalFunction.Serializer<T> serializer) {
                this.type = type;
                this.serializer = serializer;
            }

            @Override
            public void serialize(JsonObject object, ConditionalFunction<T> arg, JsonSerializationContext context) {
                super.serialize(object, arg, context);
                serializer.serialize(IDataPackObject.of(object), arg.value);
            }

            @Override
            public ConditionalFunction<T> deserialize(JsonObject object, JsonDeserializationContext context, LootItemCondition[] args) {
                T value = serializer.deserialize(IDataPackObject.of(object));
                return new ConditionalFunction<>(value, args, type);
            }
        }
    }
}
