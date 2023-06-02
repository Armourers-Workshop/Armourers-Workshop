package extensions.net.minecraft.core.Registry;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.api.registry.IRegistry;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricRegistry;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricRegistryEntry;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Available("[1.20, )")
@Extension
public class FabricProvider {

    public static final IRegistry<Block> BLOCKS = new AbstractFabricRegistry<>(Block.class, BuiltInRegistries.BLOCK);
    public static final IRegistry<Item> ITEMS = new AbstractFabricRegistry<>(Item.class, BuiltInRegistries.ITEM);
    public static final IRegistry<CreativeModeTab> ITEM_GROUPS = new AbstractFabricRegistry<>(CreativeModeTab.class, BuiltInRegistries.CREATIVE_MODE_TAB);
    public static final IRegistry<MenuType<?>> MENU_TYPES = new AbstractFabricRegistry<>(MenuType.class, BuiltInRegistries.MENU);
    public static final IRegistry<EntityType<?>> ENTITY_TYPES = new AbstractFabricRegistry<>(EntityType.class, BuiltInRegistries.ENTITY_TYPE);
    public static final IRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new AbstractFabricRegistry<>(BlockEntityType.class, BuiltInRegistries.BLOCK_ENTITY_TYPE);
    public static final IRegistry<SoundEvent> SOUND_EVENTS = new AbstractFabricRegistry<>(SoundEvent.class, BuiltInRegistries.SOUND_EVENT);
    public static final IRegistry<LootItemFunctionType> ITEM_LOOT_FUNCTIONS = new AbstractFabricRegistry<>(LootItemFunctionType.class, BuiltInRegistries.LOOT_FUNCTION_TYPE);

    public static <T extends Item> IRegistryKey<T> registerItemFA(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static <T extends IItemTag> IRegistryKey<T> registerItemTagFA(@ThisClass Class<?> clazz, String name) {
        ResourceLocation registryName = ModConstants.key(name);
        TagKey<Item> tag = TagKey.create(Registries.ITEM, registryName);
        ModLog.debug("Registering Item Tag '{}'", registryName);
        return AbstractFabricRegistryEntry.cast(registryName, (IItemTag) itemStack -> itemStack.is(tag));
    }

    public static <T extends CreativeModeTab> IRegistryKey<CreativeModeTab> registerItemGroupFA(@ThisClass Class<?> clazz, String name, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> itemProvider) {
        return ITEM_GROUPS.register(name, () -> {
            ResourceLocation registryName = ModConstants.key(name);
            return FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup." + registryName.getNamespace() + "." + registryName.getPath()))
                    .icon(() -> icon.get().get())
                    .displayItems((set, out) -> {
                        ArrayList<ItemStack> results = new ArrayList<>();
                        itemProvider.accept(results);
                        out.acceptAll(results);
                    })
                    .build();
        });
    }

    public static <T extends LootItemFunctionType> IRegistryKey<T> registerItemLootFunctionFA(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        return ITEM_LOOT_FUNCTIONS.register(name, supplier);
    }

    public static <T extends Block> IRegistryKey<T> registerBlockFA(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

    public static <T extends BlockEntity, V extends BlockEntityType<T>> IRegistryKey<V> registerBlockEntityTypeFA(@ThisClass Class<?> clazz, String name, Supplier<V> supplier) {
        return BLOCK_ENTITY_TYPES.register(name, supplier);
    }

    public static <T extends Entity, V extends EntityType<T>> IRegistryKey<V> registerEntityTypeFA(@ThisClass Class<?> clazz, String name, Supplier<V> supplier) {
        return ENTITY_TYPES.register(name, supplier);
    }

    public static <T extends EntityDataSerializer<?>> IRegistryKey<T> registerEntityDataSerializerFA(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        T value = supplier.get();
        ResourceLocation registryName = ModConstants.key(name);
        EntityDataSerializers.registerSerializer(value);
        ModLog.debug("Registering Entity Data Serializer '{}'", registryName);
        return AbstractFabricRegistryEntry.of(registryName, value);
    }

    public static <T extends AbstractContainerMenu, V extends MenuType<T>> IRegistryKey<V> registerMenuTypeFA(@ThisClass Class<?> clazz, String name, Supplier<V> supplier) {
        return MENU_TYPES.register(name, supplier);
    }

    public static <T extends SoundEvent> IRegistryKey<T> registerSoundEventFA(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        return SOUND_EVENTS.register(name, supplier);
    }
}
