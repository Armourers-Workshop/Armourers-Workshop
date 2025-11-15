package moe.plushie.armourers_workshop.init.platform;

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
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public abstract class RegistryManager {

    private static final RegistryManager INSTANCE = PlatformLoader.load(RegistryManager.class);

    public static RegistryManager getInstance() {
        return INSTANCE;
    }

    public abstract TypedProvider<IDataComponentType<?>> dataComponentTypes();

    public abstract TypedProvider<IDataAttachmentType<?>> dataAttachmentTypes();

    public abstract TypedProvider<Item> items();

    public abstract TypedProvider<CreativeModeTab> creativeModeTabs();

    public abstract TypedProvider<ILootItemFunctionType<?>> lootItemFunctions();

    public abstract TypedProvider<ITagKey<?>> itemTags();

    public abstract TypedProvider<ITagKey<?>> blockTags();

    public abstract TypedProvider<Block> blocks();

    public abstract TypedProvider<IBlockEntityType<?>> blockEntityTypes();

    public abstract TypedProvider<IBlockEntityCapability<?>> blockEntityCapabilities();

    public abstract TypedProvider<IEntityType<?>> entityTypes();

    public abstract TypedProvider<IEntityCapability<?>> entityCapabilities();

    public abstract TypedProvider<EntityDataSerializer<?>> entitySerializers();

    public abstract TypedProvider<IMenuType<?>> menuTypes();

    public abstract TypedProvider<SoundEvent> soundEvents();

    public abstract TypedProvider<IArgumentType<?>> commandArgumentTypes();
}
