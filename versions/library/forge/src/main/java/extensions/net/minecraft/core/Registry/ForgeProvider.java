package extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.api.registry.IRegistry;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistry;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistryEntry;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.syncher.EntityDataSerializer;
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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.19, )")
@Extension
public class ForgeProvider {

    public static final IRegistry<Block> BLOCKS = new Proxy<>(Block.class, ForgeRegistries.BLOCKS);
    public static final IRegistry<Item> ITEMS = new Proxy<>(Item.class, ForgeRegistries.ITEMS);
    public static final IRegistry<MenuType<?>> MENU_TYPES = new Proxy<>(MenuType.class, ForgeRegistries.MENU_TYPES);
    public static final IRegistry<EntityType<?>> ENTITY_TYPES = new Proxy<>(EntityType.class, ForgeRegistries.ENTITY_TYPES);
    public static final IRegistry<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZER = new Proxy<>(EntityDataSerializer.class, null, DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, ModConstants.MOD_ID));
    public static final IRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new Proxy<>(BlockEntityType.class, ForgeRegistries.BLOCK_ENTITY_TYPES);
    public static final IRegistry<SoundEvent> SOUND_EVENTS = new Proxy<>(SoundEvent.class, ForgeRegistries.SOUND_EVENTS);

    public static final IRegistry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = new Proxy<>(ArgumentTypeInfo.class, ForgeRegistries.COMMAND_ARGUMENT_TYPES);

    public static <T extends Item> IRegistryKey<T> registerItemFO(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static <T extends IItemTag> IRegistryKey<T> registerItemTagFO(@ThisClass Class<?> clazz, String name) {
        ResourceLocation registryName = ModConstants.key(name);
        TagKey<Item> tag = TagKey.create(ForgeRegistries.Keys.ITEMS, registryName);
        ModLog.debug("Registering Item Tag '{}'", registryName);
        return AbstractForgeRegistryEntry.cast(registryName, () -> (IItemTag) itemStack -> itemStack.is(tag));
    }

    public static <T extends CreativeModeTab> IRegistryKey<T> registerItemGroupFO(@ThisClass Class<?> clazz, String name, Supplier<Supplier<ItemStack>> icon, Consumer<List<ItemStack>> itemProvider) {
        ResourceLocation registryName = ModConstants.key(name);
        Supplier<CreativeModeTab> tab = CreativeModeTab.createCreativeModeTabFO(name, icon, itemProvider);
        ModLog.debug("Registering Creative Mode Tab '{}'", registryName);
        return AbstractForgeRegistryEntry.cast(registryName, tab);
    }

    public static <T extends Block> IRegistryKey<T> registerBlockFO(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        return BLOCKS.register(name, supplier);
    }

    public static <T extends BlockEntity, V extends BlockEntityType<T>> IRegistryKey<V> registerBlockEntityTypeFO(@ThisClass Class<?> clazz, String name, Supplier<V> supplier) {
        return BLOCK_ENTITY_TYPES.register(name, supplier);
    }

    public static <T extends Entity, V extends EntityType<T>> IRegistryKey<V> registerEntityTypeFO(@ThisClass Class<?> clazz, String name, Supplier<V> supplier) {
        return ENTITY_TYPES.register(name, supplier);
    }

    public static <T extends EntityDataSerializer<?>> IRegistryKey<T> registerEntityDataSerializerFO(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        return ENTITY_DATA_SERIALIZER.register(name, supplier);
    }

    public static <T extends AbstractContainerMenu, V extends MenuType<T>> IRegistryKey<V> registerMenuTypeFO(@ThisClass Class<?> clazz, String name, Supplier<V> supplier) {
        return MENU_TYPES.register(name, supplier);
    }

    public static <T extends SoundEvent> IRegistryKey<T> registerSoundEventFO(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
        return SOUND_EVENTS.register(name, supplier);
    }

    public static <T extends ArgumentTypeInfo<?, ?>> IRegistryKey<T> registerCommandArgumentTypeFO(@ThisClass Class<?> clazz, String name, Supplier<T> supplier) {
//        ArgumentTypeInfo<?, ?> info1 = ArgumentTypeInfos.registerByClass(argumentType, argumentInfo);
        return COMMAND_ARGUMENT_TYPES.register(name, supplier);
    }

    public static class Proxy<T> extends AbstractForgeRegistry<T> {

        private final Supplier<IForgeRegistry<T>> registry;
        private final DeferredRegister<T> deferredRegistry;

        public Proxy(Class<?> type, IForgeRegistry<T> registry) {
            this(type, () -> registry, DeferredRegister.create(registry, ModConstants.MOD_ID));
        }

        public Proxy(Class<?> type, Supplier<IForgeRegistry<T>> registry, DeferredRegister<T> deferredRegistry) {
            super(type, deferredRegistry);
            this.registry = registry;
            this.deferredRegistry = deferredRegistry;
        }

        @Override
        public <I extends T> Supplier<I> deferredRegister(String name, Supplier<? extends I> provider) {
            return deferredRegistry.register(name, provider);
        }

        @Override
        public ResourceLocation getKey(T object) {
            return registry.get().getKey(object);
        }

        @Override
        public T getValue(ResourceLocation registryName) {
            return registry.get().getValue(registryName);
        }
    }
}
