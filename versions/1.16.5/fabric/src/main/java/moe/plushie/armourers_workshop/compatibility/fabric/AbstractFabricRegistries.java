package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.common.*;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AbstractFabricRegistries {

    public static final IRegistryProvider<Block> BLOCKS = wrap(Registry.BLOCK);
    public static final IRegistryProvider<Item> ITEMS = wrap(Registry.ITEM);
    public static final IRegistryProvider<MenuType<?>> MENU_TYPES = wrap(Registry.MENU);
    public static final IRegistryProvider<EntityType<?>> ENTITY_TYPES = wrap(Registry.ENTITY_TYPE);
    public static final IRegistryProvider<BlockEntityType<?>> BLOCK_ENTITY_TYPES = wrap(Registry.BLOCK_ENTITY_TYPE);
    public static final IRegistryProvider<SoundEvent> SOUND_EVENTS = wrap(Registry.SOUND_EVENT);

    public static final IItemTagRegistry<Item> ITEM_TAGS = name -> () ->  {
        ResourceLocation registryName = ModConstants.key(name);
        Tag<Item> tag = TagRegistry.item(registryName);
        return new IItemTagKey<Item>() {

            @Override
            public Predicate<ItemStack> get() {
                return itemStack -> itemStack.getItem().is(tag);
            }

            @Override
            public ResourceLocation getRegistryName() {
                return registryName;
            }
        };
    };

    private static <T, R extends Registry<T>> IRegistryProvider<T> wrap(R registry) {
        return new IRegistryProvider<T>() {
            @Override
            public int getId(ResourceLocation registryName) {
                return registry.getId(getValue(registryName));
            }

            @Override
            public ResourceLocation getKey(T object) {
                return registry.getKey(object);
            }

            @Override
            public T getValue(ResourceLocation registryName) {
                return registry.get(registryName);
            }

            @Override
            public <I extends T> Supplier<I> register(String name, Supplier<? extends I> provider) {
                I value = provider.get();
                ResourceLocation registryName = ModConstants.key(name);
                Registry.register(registry, registryName, value);
                return () -> value;
            }
        };
    }

    public static CreativeModeTab registerCreativeModeTab(ResourceLocation registryName, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> results) {
        return FabricItemGroupBuilder.create(registryName)
                .icon(() -> icon.get().get())
                .appendItems(results)
                .build();
    }
}
