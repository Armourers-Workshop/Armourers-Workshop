package moe.plushie.armourers_workshop.init.platform.fabric;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.api.other.IRegistryObject;
import moe.plushie.armourers_workshop.init.ModLog;
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

    private static final ImmutableMap<Class<?>, Registry<?>> REGISTRIES = new ImmutableMap.Builder<Class<?>, Registry<?>>()
            .put(Block.class, new RegistryProxy<>(net.minecraft.core.Registry.BLOCK))
            .put(Item.class, new RegistryProxy<>(net.minecraft.core.Registry.ITEM))
            .put(MenuType.class, new RegistryProxy<>(net.minecraft.core.Registry.MENU))
            .put(EntityType.class, new RegistryProxy<>(net.minecraft.core.Registry.ENTITY_TYPE))
            .put(BlockEntityType.class, new RegistryProxy<>(net.minecraft.core.Registry.BLOCK_ENTITY_TYPE))
            .put(SoundEvent.class, new RegistryProxy<>(net.minecraft.core.Registry.SOUND_EVENT))
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

    public static class RegistryProxy<T, R extends net.minecraft.core.Registry<T>> extends Registry<T> {

        protected final R registry;
        protected final LinkedHashSet<IRegistryObject<T>> entriesView = new LinkedHashSet<>();

        protected RegistryProxy(R registry) {
            this.registry = registry;
        }

        @Override
        public T get(ResourceLocation registryName) {
            return registry.get(registryName);
        }

        @Override
        public ResourceLocation getKey(T object) {
            return registry.getKey(object);
        }

        @Override
        public Collection<IRegistryObject<T>> getEntries() {
            return entriesView;
        }

        @Override
        public <I extends T> IRegistryObject<I> register(String name, Supplier<? extends I> sup) {
            ResourceLocation registryName = ArmourersWorkshop.getResource(name);
            ModLog.debug("Registering '{}'", registryName);
            I value = R.register(registry, registryName, sup.get());
            IRegistryObject<I> object = new IRegistryObject<I>() {
                @Override
                public ResourceLocation getRegistryName() {
                    return registryName;
                }

                @Override
                public I get() {
                    return value;
                }
            };
            entriesView.add(ObjectUtils.unsafeCast(object));
            return object;
        }
    }
}
