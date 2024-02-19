package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.api.registry.IRegistry;
import moe.plushie.armourers_workshop.utils.TypedRegistry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Available("[1.21, )")
public class AbstractForgeRegistries {

    public static final IRegistry<Block> BLOCKS = AbstractForgeRegistry.create(Block.class, BuiltInRegistries.BLOCK);
    public static final IRegistry<Item> ITEMS = AbstractForgeRegistry.create(Item.class, BuiltInRegistries.ITEM);
    public static final IRegistry<MenuType<?>> MENU_TYPES = AbstractForgeRegistry.create(MenuType.class, BuiltInRegistries.MENU);
    public static final IRegistry<EntityType<?>> ENTITY_TYPES = AbstractForgeRegistry.create(EntityType.class, BuiltInRegistries.ENTITY_TYPE);
    public static final IRegistry<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZER = AbstractForgeRegistry.create(EntityDataSerializer.class, NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS);
    public static final IRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = AbstractForgeRegistry.create(BlockEntityType.class, BuiltInRegistries.BLOCK_ENTITY_TYPE);
    public static final IRegistry<SoundEvent> SOUND_EVENTS = AbstractForgeRegistry.create(SoundEvent.class, BuiltInRegistries.SOUND_EVENT);
    public static final IRegistry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = AbstractForgeRegistry.create(ArgumentTypeInfo.class, BuiltInRegistries.COMMAND_ARGUMENT_TYPE);

    public static final IRegistry<LootItemFunctionType> ITEM_LOOT_FUNCTIONS = AbstractForgeRegistry.create(LootItemFunctionType.class, BuiltInRegistries.LOOT_FUNCTION_TYPE);
    public static final IRegistry<CreativeModeTab> ITEM_GROUPS = AbstractForgeRegistry.create(CreativeModeTab.class, BuiltInRegistries.CREATIVE_MODE_TAB);
    public static final IRegistry<IItemTag> ITEM_TAGS = TypedRegistry.factory(TagKey.class, registryName -> {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, registryName);
        return itemStack -> itemStack.is(tag);
    });

    public static final IRegistry<AttachmentType<?>> ATTACHMENT_TYPES = AbstractForgeRegistry.create(AttachmentType.class, NeoForgeRegistries.ATTACHMENT_TYPES);
}
