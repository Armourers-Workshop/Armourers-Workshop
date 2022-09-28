package moe.plushie.armourers_workshop.init.platform.forge;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class RegistryManagerImpl {

    private static final ImmutableMap<Class<?>, Registry<?>> REGISTRIES = new ImmutableMap.Builder<Class<?>, Registry<?>>()
            .put(Block.class, new RegistryProxy<>(AbstractForgeRegistries.BLOCKS))
            .put(Item.class, new RegistryProxy<>(AbstractForgeRegistries.ITEMS))
            .put(MenuType.class, new RegistryProxy<>(AbstractForgeRegistries.MENUS))
            .put(EntityType.class, new RegistryProxy<>(AbstractForgeRegistries.ENTITIES))
            .put(BlockEntityType.class, new RegistryProxy<>(AbstractForgeRegistries.BLOCK_ENTITIES))
            .put(SoundEvent.class, new RegistryProxy<>(AbstractForgeRegistries.SOUND_EVENTS))
            .build();

    public static <T> Registry<T> makeRegistry(Class<? super T> clazz) {
        // for vanilla supported registry types, we directly forward to vanilla.
        Registry<?> registry = REGISTRIES.get(clazz);
        if (registry != null) {
            return ObjectUtils.unsafeCast(registry);
        }

        // if an error is thrown here, that mean vanilla broke something.
        throw new AssertionError("not supported registry entry of the " + clazz);
    }

    public static class RegistryProxy<T extends IForgeRegistryEntry<T>, R extends IForgeRegistry<T>> extends Registry<T> {

        protected final R registry;
        protected final DeferredRegister<T> deferredRegister;
        protected final LinkedHashSet<IRegistryKey<T>> entriesView = new LinkedHashSet<>();

        protected RegistryProxy(R registry) {
            this.registry = registry;
            this.deferredRegister = DeferredRegister.create(registry, ModConstants.MOD_ID);
            this.deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        @Override
        public T get(ResourceLocation registryName) {
            return registry.getValue(registryName);
        }

        @Override
        public ResourceLocation getKey(T object) {
            return registry.getKey(object);
        }

        @Override
        public Collection<IRegistryKey<T>> getEntries() {
            return entriesView;
        }

        @Override
        public <I extends T> IRegistryKey<I> register(String name, Supplier<? extends I> provider) {
            ResourceLocation registryName = ModConstants.key(name);
            ModLog.debug("Registering '{}'", registryName);
            Supplier<I> value = deferredRegister.register(name, provider);
            IRegistryKey<I> object = new IRegistryKey<I>() {
                @Override
                public ResourceLocation getRegistryName() {
                    return registryName;
                }

                @Override
                public I get() {
                    return value.get();
                }
            };
            entriesView.add(ObjectUtils.unsafeCast(object));
            return object;
        }
    }
}
