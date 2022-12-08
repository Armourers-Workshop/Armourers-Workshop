package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IRegistry;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public abstract class AbstractForgeRegistries extends ForgeRegistries {

    public static <T> IRegistry<T> wrap(IForgeRegistry<T> registry) {
        DeferredRegister<T> registry1 = DeferredRegister.create(registry, ModConstants.MOD_ID);
        registry1.register(FMLJavaModLoadingContext.get().getModEventBus());
        return new IRegistry<T>() {

            @Override
            public int getId(ResourceLocation registryName) {
                // we need query the registry entry id in the forge.
                if (registry instanceof ForgeRegistry<T>) {
                    return ((ForgeRegistry<T>) registry).getID(registryName);
                }
                return 0;
            }

            @Override
            public ResourceLocation getKey(T object) {
                return registry.getKey(object);
            }

            @Override
            public T getValue(ResourceLocation registryName) {
                return registry.getValue(registryName);
            }

            @Override
            public <I extends T> Supplier<I> register(String name, Supplier<? extends I> provider) {
                return registry1.register(name, provider);
            }
        };
    }

    public static boolean isModBusEvent(Class<?> clazz) {
        return IModBusEvent.class.isAssignableFrom(clazz);
    }
}
