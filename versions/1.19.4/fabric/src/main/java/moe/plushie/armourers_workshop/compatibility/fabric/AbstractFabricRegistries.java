package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.common.IItemTagKey;
import moe.plushie.armourers_workshop.api.common.IItemTagRegistry;
import moe.plushie.armourers_workshop.api.common.IRegistryProvider;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AbstractFabricRegistries {

    public static final IRegistryProvider<Block> BLOCKS = wrap(BuiltInRegistries.BLOCK);
    public static final IRegistryProvider<Item> ITEMS = wrap(BuiltInRegistries.ITEM);
    public static final IRegistryProvider<MenuType<?>> MENU_TYPES = wrap(BuiltInRegistries.MENU);
    public static final IRegistryProvider<EntityType<?>> ENTITY_TYPES = wrap(BuiltInRegistries.ENTITY_TYPE);
    public static final IRegistryProvider<BlockEntityType<?>> BLOCK_ENTITY_TYPES = wrap(BuiltInRegistries.BLOCK_ENTITY_TYPE);
    public static final IRegistryProvider<SoundEvent> SOUND_EVENTS = wrap(BuiltInRegistries.SOUND_EVENT);

    public static final IItemTagRegistry<Item> ITEM_TAGS = name -> () -> {
        ResourceLocation registryName = ModConstants.key(name);
        TagKey<Item> tag = TagKey.create(Registries.ITEM, registryName);
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

    public static CreativeModeTab registerCreativeModeTab(ResourceLocation registryName, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> consumer) {
        return FabricItemGroup.builder(registryName)
                .title(Component.translatable("itemGroup." + registryName.getNamespace() + "." + registryName.getPath()))
                .icon(() -> icon.get().get())
                //.displayItems((set, out, bl) -> {
                .displayItems((set, out) -> {
                    ArrayList<ItemStack> results = new ArrayList<>();
                    consumer.accept(results);
                    out.acceptAll(results);
                })
                .build();
    }
}
