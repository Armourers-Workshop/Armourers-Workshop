package moe.plushie.armourers_workshop.core.registry;

import moe.plushie.armourers_workshop.api.registry.IRegistryObject;
import moe.plushie.armourers_workshop.init.platform.RegistryManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Collection;
import java.util.function.Supplier;

public abstract class Registry<T> {

    public static final Registry<SoundEvent> SOUND_EVENT = RegistryManager.makeRegistry(SoundEvent.class);

    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = RegistryManager.makeRegistry(BlockEntityType.class);

    public static final Registry<EntityType<?>> ENTITY_TYPE = RegistryManager.makeRegistry(EntityType.class);

    public static final Registry<MenuType<?>> MENU_TYPE = RegistryManager.makeRegistry(MenuType.class);

    public static final Registry<Item> ITEM = RegistryManager.makeRegistry(Item.class);

    public static final Registry<Block> BLOCK = RegistryManager.makeRegistry(Block.class);

    public abstract T get(ResourceLocation registryName);

    public abstract ResourceLocation getKey(T object);

    public abstract Collection<IRegistryObject<T>> getEntries();

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name The new entry's name, it will automatically have the modid prefixed.
     * @param sup  A factory for the new entry, it should return a new instance every time it is called.
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     */
    public abstract <I extends T> IRegistryObject<I> register(final String name, final Supplier<? extends I> sup);
}
