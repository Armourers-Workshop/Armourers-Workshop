package moe.plushie.armourers_workshop.init.platform.runtime;

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

public interface CommonRegistryFactory {

    TypedProvider<IDataComponentType<?>> dataComponentType();

    TypedProvider<IDataAttachmentType<?>> dataAttachmentType();

    TypedProvider<Item> item();

    TypedProvider<CreativeModeTab> creativeModeTab();

    TypedProvider<ILootItemFunctionType<?>> lootItemFunctionType();

    TypedProvider<ITagKey<?>> itemTag();

    TypedProvider<ITagKey<?>> blockTag();

    TypedProvider<Block> block();

    TypedProvider<IBlockEntityType<?>> blockEntityType();

    TypedProvider<IBlockEntityCapability<?>> blockEntityCapability();

    TypedProvider<IEntityType<?>> entityType();

    TypedProvider<IEntityCapability<?>> entityCapability();

    TypedProvider<EntityDataSerializer<?>> entityDataSerializer();

    TypedProvider<IMenuType<?>> menuType();

    TypedProvider<SoundEvent> soundEvent();

    TypedProvider<IArgumentType<?>> commandArgumentType();
}
