package moe.plushie.armourers_workshop.init.platform.forge;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.common.IRegistry;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.IRegistryProvider;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.forge.addon.BukkitAddon;
import moe.plushie.armourers_workshop.utils.LazyValue;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class RegistryManagerImpl {

    private static final ImmutableMap<Class<?>, IRegistry<?>> REGISTRIES = new ImmutableMap.Builder<Class<?>, IRegistry<?>>()
            .put(Block.class, new RegistryProxy<>("blocks", AbstractForgeRegistries.BLOCKS))
            .put(Item.class, new RegistryProxy<>("items", AbstractForgeRegistries.ITEMS))
            .put(MenuType.class, new RegistryProxy<>("menuTypes", AbstractForgeRegistries.MENU_TYPES))
            .put(EntityType.class, new RegistryProxy<>("entityTypes", AbstractForgeRegistries.ENTITY_TYPES))
            .put(BlockEntityType.class, new RegistryProxy<>("blockEntityTypes", AbstractForgeRegistries.BLOCK_ENTITY_TYPES))
            .put(SoundEvent.class, new RegistryProxy<>("soundEvents", AbstractForgeRegistries.SOUND_EVENTS))
            .build();

    public static <T> IRegistry<T> makeRegistry(Class<? super T> clazz) {
        // for vanilla supported registry types, we directly forward to vanilla.
        IRegistry<?> registry = REGISTRIES.get(clazz);
        if (registry != null) {
            return ObjectUtils.unsafeCast(registry);
        }
        // if an error is thrown here, that mean vanilla broke something.
        throw new AssertionError("not supported registry entry of the " + clazz);
    }

    public static class RegistryProxy<T> implements IRegistry<T> {

        protected final String category;
        protected final IRegistryProvider<T> registry;
        protected final LinkedHashSet<IRegistryKey<T>> entriesView = new LinkedHashSet<>();

        protected RegistryProxy(String category, IRegistryProvider<T> registry) {
            this.category = category;
            this.registry = registry;
        }

        @Override
        public int getId(ResourceLocation registryName) {
            return 0;
        }

        @Override
        public T getValue(ResourceLocation registryName) {
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
            LazyValue<? extends I> lazyProvider = LazyValue.of(provider);
            ResourceLocation registryName = ModConstants.key(name);
            ModLog.debug("Registering '{}'", registryName);
            Supplier<I> value = registry.register(name, lazyProvider);
            IRegistryKey<I> object = new IRegistryKey<I>() {
                @Override
                public ResourceLocation getRegistryName() {
                    return registryName;
                }

                @Override
                public I get() {
                    return lazyProvider.get();
                }
            };
            entriesView.add(ObjectUtils.unsafeCast(object));
            BukkitAddon.register(category, registryName, registry::getId);
            return object;
        }
    }
}
