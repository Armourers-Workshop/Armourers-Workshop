package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.utils.TypedRegistry;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.component.DataComponentType;
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

import manifold.ext.rt.api.auto;

@Available("[1.21, )")
public class AbstractForgeRegistries {

    public static final auto DATA_COMPONENT_TYPES = AbstractForgeRegistry.create("Data Component Type", DataComponentType.class, Registries.DATA_COMPONENT_TYPE);

    public static final auto ITEMS = AbstractForgeRegistry.create("Item", Item.class, BuiltInRegistries.ITEM);
    public static final auto ITEM_GROUPS = AbstractForgeRegistry.create("Creative Mode Tab", CreativeModeTab.class, BuiltInRegistries.CREATIVE_MODE_TAB);
    public static final auto ITEM_LOOT_FUNCTIONS = AbstractForgeRegistry.create("Loot Function Type", LootItemFunctionType.class, BuiltInRegistries.LOOT_FUNCTION_TYPE);
    public static final auto ITEM_TAGS = TypedRegistry.factory("Item Tag", IItemTag.class, registryName -> {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, registryName);
        return itemStack -> itemStack.is(tag);
    });

    public static final auto BLOCKS = AbstractForgeRegistry.create("Block", Block.class, BuiltInRegistries.BLOCK);
    public static final auto BLOCK_ENTITY_TYPES = AbstractForgeRegistry.create("Block Entity Type",BlockEntityType.class, BuiltInRegistries.BLOCK_ENTITY_TYPE);

    public static final auto ENTITY_TYPES = AbstractForgeRegistry.create("Entity Type", EntityType.class, BuiltInRegistries.ENTITY_TYPE);
    public static final auto ENTITY_DATA_SERIALIZER = AbstractForgeRegistry.create("Entity Data Serializer", EntityDataSerializer.class, NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS);

    public static final auto MENU_TYPES = AbstractForgeRegistry.create("Menu Type", MenuType.class, BuiltInRegistries.MENU);
    public static final auto SOUND_EVENTS = AbstractForgeRegistry.create("Sound Event", SoundEvent.class, BuiltInRegistries.SOUND_EVENT);
    public static final auto COMMAND_ARGUMENT_TYPES = AbstractForgeRegistry.create("Argument Type", ArgumentTypeInfo.class, BuiltInRegistries.COMMAND_ARGUMENT_TYPE);

    public static final auto ATTACHMENT_TYPES = AbstractForgeRegistry.create("Data Attachment Type", AttachmentType.class, NeoForgeRegistries.ATTACHMENT_TYPES);
}
