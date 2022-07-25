package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.registry.IRegistryObject;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class RegistryManagerImpl {

    public static <T> Registry<T> makeRegistry(Class<? super T> clazz) {
        // for forge supported registry types, we directly forward to forge.
        if (IForgeRegistryEntry.class.isAssignableFrom(clazz)) {
            return ObjectUtils.unsafeCast(makeForgeRegistry(ObjectUtils.unsafeCast(clazz)));
        }
        // if an error is thrown here, that mean forge broke something.
        throw new AssertionError("not supported registry entry of the " + clazz);
    }

    public static <T extends IForgeRegistryEntry<T>> Registry<T> makeForgeRegistry(Class<T> clazz) {
        DeferredRegister<T> register = DeferredRegister.create(clazz, ArmourersWorkshop.MOD_ID);
        register.register(FMLJavaModLoadingContext.get().getModEventBus());
        return new Registry<T>() {
            private final LinkedHashSet<IRegistryObject<T>> entriesView = new LinkedHashSet<>();

            @Override
            public T get(ResourceLocation registryName) {
                return net.minecraftforge.fml.RegistryObject.of(registryName, clazz, ArmourersWorkshop.MOD_ID).get();
            }

            @Override
            public ResourceLocation getKey(T object) {
                return object.getRegistryName();
            }

            @Override
            public Collection<IRegistryObject<T>> getEntries() {
                return entriesView;
            }

            @Override
            public <I extends T> IRegistryObject<I> register(String name, Supplier<? extends I> sup) {
                return put(name, register.register(name, sup));
            }

            private <I extends T> IRegistryObject<I> put(String name, net.minecraftforge.fml.RegistryObject<I> object) {
                IRegistryObject<I> newObjet = new IRegistryObject<I>() {

                    @Override
                    public I get() {
                        return object.get();
                    }

                    @Override
                    public ResourceLocation getRegistryName() {
                        return object.getId();
                    }

                    @Override
                    public String toString() {
                        return object.toString();
                    }
                };
                entriesView.add(ObjectUtils.unsafeCast(newObjet));
                return newObjet;
            }
        };
    }
}
