package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IItemTagKey;
import moe.plushie.armourers_workshop.api.common.IRegistry;
import moe.plushie.armourers_workshop.api.common.IItemTagRegistry;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractForgeRegistries {

    public static final IRegistry<Block> BLOCKS = wrap(ForgeRegistries.BLOCKS);
    public static final IRegistry<Item> ITEMS = wrap(ForgeRegistries.ITEMS);
    public static final IRegistry<MenuType<?>> MENU_TYPES = wrap(ForgeRegistries.CONTAINERS);
    public static final IRegistry<EntityType<?>> ENTITY_TYPES = wrap(ForgeRegistries.ENTITIES);
    public static final IRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = wrap(ForgeRegistries.TILE_ENTITIES);
    public static final IRegistry<SoundEvent> SOUND_EVENTS = wrap(ForgeRegistries.SOUND_EVENTS);

    public static final IItemTagRegistry<Item> ITEM_TAGS = name -> () -> {
        ResourceLocation registryName = ModConstants.key(name);
        Tag<Item> tag = ItemTags.createOptional(registryName);
        return new IItemTagKey<Item>() {
            @Override
            public ResourceLocation getRegistryName() {
                return registryName;
            }

            @Override
            public Predicate<ItemStack> get() {
                return itemStack -> itemStack.getItem().is(tag);
            }
        };
    };

    public static <T extends IForgeRegistryEntry<T>> IRegistry<T> wrap(IForgeRegistry<T> registry) {
        DeferredRegister<T> registry1 = DeferredRegister.create(registry, ModConstants.MOD_ID);
        registry1.register(FMLJavaModLoadingContext.get().getModEventBus());
        return new IRegistry<T>() {

            @Override
            public int getId(ResourceLocation registryName) {
                // we need query the registry entry id in the forge.
                if (registry instanceof ForgeRegistry) {
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
}
