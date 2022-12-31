package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IItemTagKey;
import moe.plushie.armourers_workshop.api.common.IItemTagRegistry;
import moe.plushie.armourers_workshop.api.common.IRegistryProvider;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.syncher.EntityDataSerializer;
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
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractForgeRegistries {

    public static final IRegistryProvider<Block> BLOCKS = wrap(ForgeRegistries.BLOCKS);
    public static final IRegistryProvider<Item> ITEMS = wrap(ForgeRegistries.ITEMS);
    public static final IRegistryProvider<MenuType<?>> MENU_TYPES = wrap(ForgeRegistries.CONTAINERS);
    public static final IRegistryProvider<EntityType<?>> ENTITY_TYPES = wrap(ForgeRegistries.ENTITIES);
    public static final IRegistryProvider<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = createDataSerializer();
    public static final IRegistryProvider<BlockEntityType<?>> BLOCK_ENTITY_TYPES = wrap(ForgeRegistries.BLOCK_ENTITIES);
    public static final IRegistryProvider<SoundEvent> SOUND_EVENTS = wrap(ForgeRegistries.SOUND_EVENTS);

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

    public static <T extends IForgeRegistryEntry<T>> IRegistryProvider<T> wrap(IForgeRegistry<T> registry) {
        return new Proxy<>(() -> registry, DeferredRegister.create(registry, ModConstants.MOD_ID));
    }

    public static boolean isModBusEvent(Class<?> clazz) {
        return IModBusEvent.class.isAssignableFrom(clazz);
    }

    public static Supplier<CreativeModeTab> registerCreativeModeTab(ResourceLocation registryName, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> itemProvider) {
        CreativeModeTab tab = new CreativeModeTab(registryName.getNamespace() + "." + registryName.getPath()) {
            @Override
            public ItemStack makeIcon() {
                return icon.get().get();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> arg) {
                itemProvider.accept(arg);
            }
        };
        return () -> tab;
    }

    private static IRegistryProvider<EntityDataSerializer<?>> createDataSerializer() {
        IRegistryProvider<DataSerializerEntry> registry = new Proxy<>(ForgeRegistries.DATA_SERIALIZERS, DeferredRegister.create(ForgeRegistries.Keys.DATA_SERIALIZERS, ModConstants.MOD_ID));
        return new IRegistryProvider<EntityDataSerializer<?>>() {
            @Override
            public int getId(ResourceLocation registryName) {
                return registry.getId(registryName);
            }

            @Override
            public ResourceLocation getKey(EntityDataSerializer<?> object) {
                return null;
            }

            @Override
            public EntityDataSerializer<?> getValue(ResourceLocation registryName) {
                return null;
            }

            @Override
            public <I extends EntityDataSerializer<?>> Supplier<I> register(String name, Supplier<? extends I> provider) {
                Supplier<DataSerializerEntry> sup = registry.register(name, () -> new DataSerializerEntry(provider.get()));
                return () -> ObjectUtils.unsafeCast(sup.get().getSerializer());
            }
        };
    }

    private static class Proxy<T extends IForgeRegistryEntry<T>> implements IRegistryProvider<T> {

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
