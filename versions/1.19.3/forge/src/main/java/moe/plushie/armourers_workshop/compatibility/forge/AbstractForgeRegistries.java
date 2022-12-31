package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IItemTagKey;
import moe.plushie.armourers_workshop.api.common.IItemTagRegistry;
import moe.plushie.armourers_workshop.api.common.IRegistryProvider;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractForgeRegistries {

    public static final IRegistryProvider<Block> BLOCKS = wrap(ForgeRegistries.BLOCKS);
    public static final IRegistryProvider<Item> ITEMS = wrap(ForgeRegistries.ITEMS);
    public static final IRegistryProvider<MenuType<?>> MENU_TYPES = wrap(ForgeRegistries.MENU_TYPES);
    public static final IRegistryProvider<EntityType<?>> ENTITY_TYPES = wrap(ForgeRegistries.ENTITY_TYPES);
    public static final IRegistryProvider<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = wrap(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, ForgeRegistries.ENTITY_DATA_SERIALIZERS);
    public static final IRegistryProvider<BlockEntityType<?>> BLOCK_ENTITY_TYPES = wrap(ForgeRegistries.BLOCK_ENTITY_TYPES);
    public static final IRegistryProvider<SoundEvent> SOUND_EVENTS = wrap(ForgeRegistries.SOUND_EVENTS);
    public static final IRegistryProvider<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = wrap(ForgeRegistries.COMMAND_ARGUMENT_TYPES);

    public static final IItemTagRegistry<Item> ITEM_TAGS = name -> () -> {
        ResourceLocation registryName = ModConstants.key(name);
        TagKey<Item> tag = TagKey.create(ForgeRegistries.Keys.ITEMS, registryName);
        return new IItemTagKey<Item>() {
            @Override
            public ResourceLocation getRegistryName() {
                return registryName;
            }

            @Override
            public Predicate<ItemStack> get() {
                return itemStack -> itemStack.is(tag);
            }
        };
    };

    public static <T> IRegistryProvider<T> wrap(IForgeRegistry<T> registry) {
        return new Proxy<>(() -> registry, DeferredRegister.create(registry, ModConstants.MOD_ID));
    }

    public static <T> IRegistryProvider<T> wrap(ResourceKey<Registry<T>> key, Supplier<IForgeRegistry<T>> registry) {
        return new Proxy<>(registry, DeferredRegister.create(key, ModConstants.MOD_ID));
    }

    public static boolean isModBusEvent(Class<?> clazz) {
        return IModBusEvent.class.isAssignableFrom(clazz);
    }

    public static Supplier<CreativeModeTab> registerCreativeModeTab(ResourceLocation registryName, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> itemProvider) {
        CreativeModeTab[] tabs = {null};
        NotificationCenterImpl.observer(CreativeModeTabEvent.Register.class, event -> tabs[0] = event.registerCreativeModeTab(registryName, configurator -> {
            configurator.title(Component.translatable("itemGroup." + registryName.getNamespace() + "." + registryName.getPath()));
            configurator.icon(() -> icon.get().get());
            configurator.displayItems((features, output, flag) -> {
                ArrayList<ItemStack> list = new ArrayList<>();
                itemProvider.accept(list);
                output.acceptAll(list);
            });
        }));
        return () -> tabs[0];
    }

    private static class Proxy<T> implements IRegistryProvider<T> {

        private final Supplier<IForgeRegistry<T>> registry;
        private final DeferredRegister<T> registry1;

        public Proxy(Supplier<IForgeRegistry<T>> registry, DeferredRegister<T> registry1) {
            this.registry = registry;
            this.registry1 = registry1;
            // auto register
            registry1.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        @Override
        public int getId(ResourceLocation registryName) {
            // we need query the registry entry id in the forge.
            if (registry.get() instanceof ForgeRegistry<T>) {
                return ((ForgeRegistry<T>) registry.get()).getID(registryName);
            }
            return 0;
        }

        @Override
        public ResourceLocation getKey(T object) {
            return registry.get().getKey(object);
        }

        @Override
        public T getValue(ResourceLocation registryName) {
            return registry.get().getValue(registryName);
        }

        @Override
        public <I extends T> Supplier<I> register(String name, Supplier<? extends I> provider) {
            return registry1.register(name, provider);
        }
    }
}
