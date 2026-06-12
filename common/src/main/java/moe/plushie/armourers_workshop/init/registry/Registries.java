package moe.plushie.armourers_workshop.init.registry;

import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.common.ILootItemFunctionType;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.api.common.ITagKey;
import moe.plushie.armourers_workshop.api.core.IDataAttachmentType;
import moe.plushie.armourers_workshop.api.core.IDataComponentType;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.platform.Platform;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonRegistryFactory;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

@SuppressWarnings("unused")
public class Registries {

    public static final TypedRegistry<IDataComponentType<?>> DATA_COMPONENT_TYPES = create("Data Component Type", CommonRegistryFactory::dataComponentType);
    public static final TypedRegistry<IDataAttachmentType<?>> DATA_ATTACHMENT_TYPES = create("Data Attachment Type", CommonRegistryFactory::dataAttachmentType);

    public static final TypedRegistry<Item> ITEMS = create("Item", CommonRegistryFactory::item);
    public static final TypedRegistry<CreativeModeTab> CREATIVE_MODE_TABS = create("Creative Mode Tab", CommonRegistryFactory::creativeModeTab);
    public static final TypedRegistry<ILootItemFunctionType<?>> ITEM_LOOT_FUNCTIONS = create("Item Loot Function Type", CommonRegistryFactory::lootItemFunctionType);

    public static final TypedRegistry<ITagKey<?>> ITEM_TAGS = create("Item Tag", CommonRegistryFactory::itemTag);
    public static final TypedRegistry<ITagKey<?>> BLOCK_TAGS = create("Block Tag", CommonRegistryFactory::blockTag);

    public static final TypedRegistry<Block> BLOCKS = create("Block", CommonRegistryFactory::block);
    public static final TypedRegistry<IBlockEntityType<?>> BLOCK_ENTITY_TYPES = create("Block Entity Type", CommonRegistryFactory::blockEntityType);
    public static final TypedRegistry<IBlockEntityCapability<?>> BLOCK_ENTITY_CAPABILITIES = create("Block Entity Capability", CommonRegistryFactory::blockEntityCapability);

    public static final TypedRegistry<IEntityType<?>> ENTITY_TYPES = create("Entity Type", CommonRegistryFactory::entityType);
    public static final TypedRegistry<IEntityCapability<?>> ENTITY_CAPABILITIES = create("Entity Capability", CommonRegistryFactory::entityCapability);
    public static final TypedRegistry<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = create("Entity Data Serializer", CommonRegistryFactory::entityDataSerializer);

    public static final TypedRegistry<IMenuType<?>> MENU_TYPES = create("Menu Type", CommonRegistryFactory::menuType);
    public static final TypedRegistry<SoundEvent> SOUND_EVENTS = create("Sound Event", CommonRegistryFactory::soundEvent);

    public static final TypedRegistry<IArgumentType<?>> COMMAND_ARGUMENT_TYPES = create("Command Argument Type", CommonRegistryFactory::commandArgumentType);

    private static <T> TypedRegistry<T> create(String name, Function<CommonRegistryFactory, TypedProvider<T>> provider) {
        return new TypedRegistry<>(name, provider.apply(Platform.get().common().registry()));
    }
}

