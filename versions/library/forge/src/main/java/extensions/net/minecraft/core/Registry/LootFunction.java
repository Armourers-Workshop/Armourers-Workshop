package extensions.net.minecraft.core.Registry;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IRegistry;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistry;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Available("[1.20, )")
@Extension
public class LootFunction {

    public static final IRegistry<LootItemFunctionType> ITEM_LOOT_FUNCTIONS = new Proxy<>(LootItemFunctionType.class, DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, ModConstants.MOD_ID));

    public static <T extends LootItemFunctionType> IRegistryKey<T> registerItemLootFunctionFO(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        return ITEM_LOOT_FUNCTIONS.register(name, supplier);
    }

    public static class Proxy<T> extends AbstractForgeRegistry<T> {

        private final DeferredRegister<T> deferredRegistry;


        public Proxy(Class<?> type, DeferredRegister<T> deferredRegistry) {
            super(type, deferredRegistry);
            this.deferredRegistry = deferredRegistry;
        }

        @Override
        public <I extends T> Supplier<I> deferredRegister(String name, Supplier<? extends I> provider) {
            return deferredRegistry.register(name, provider);
        }

        @Override
        public ResourceLocation getKey(T object) {
            return null;
        }

        @Override
        public T getValue(ResourceLocation registryName) {
            return null;
        }
    }
}
