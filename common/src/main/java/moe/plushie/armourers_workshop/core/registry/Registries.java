package moe.plushie.armourers_workshop.core.registry;

import moe.plushie.armourers_workshop.api.common.IRegistry;
import moe.plushie.armourers_workshop.init.platform.RegistryManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class Registries {

    public static final IRegistry<SoundEvent> SOUND_EVENT = RegistryManager.makeRegistry(SoundEvent.class);

    public static final IRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = RegistryManager.makeRegistry(BlockEntityType.class);

    public static final IRegistry<EntityType<?>> ENTITY_TYPE = RegistryManager.makeRegistry(EntityType.class);

    public static final IRegistry<MenuType<?>> MENU_TYPE = RegistryManager.makeRegistry(MenuType.class);

    public static final IRegistry<Item> ITEM = RegistryManager.makeRegistry(Item.class);

    public static final IRegistry<Block> BLOCK = RegistryManager.makeRegistry(Block.class);
}
