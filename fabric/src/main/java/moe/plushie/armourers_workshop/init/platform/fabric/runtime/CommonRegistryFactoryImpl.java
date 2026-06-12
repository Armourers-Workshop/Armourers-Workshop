package moe.plushie.armourers_workshop.init.platform.fabric.runtime;

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
import moe.plushie.armourers_workshop.init.platform.runtime.CommonRegistryFactory;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CommonRegistryFactoryImpl implements CommonRegistryFactory {

    @Override
    public TypedProvider<IDataComponentType<?>> dataComponentType() {
        return Registry.createDataComponentTypeRegistryFA();
    }

    @Override
    public TypedProvider<IDataAttachmentType<?>> dataAttachmentType() {
        return TypedProvider.passthrough(); // not support in the fabric.
    }

    @Override
    public TypedProvider<Item> item() {
        return Registry.createItemRegistryFA();
    }

    @Override
    public TypedProvider<CreativeModeTab> creativeModeTab() {
        return Registry.createCreativeModeTabRegistryFA();
    }

    @Override
    public TypedProvider<ILootItemFunctionType<?>> lootItemFunctionType() {
        return Registry.createLootItemFunctionTypeRegistryFA();
    }

    @Override
    public TypedProvider<ITagKey<?>> itemTag() {
        return Registry.createItemTagRegistryFA();
    }

    @Override
    public TypedProvider<ITagKey<?>> blockTag() {
        return Registry.createBlockTagRegistryFA();
    }

    @Override
    public TypedProvider<Block> block() {
        return Registry.createBlockRegistryFA();
    }

    @Override
    public TypedProvider<IBlockEntityType<?>> blockEntityType() {
        return Registry.createBlockEntityTypeRegistryFA();
    }

    @Override
    public TypedProvider<IBlockEntityCapability<?>> blockEntityCapability() {
        return Registry.createBlockEntityCapabilityRegistryFA();
    }

    @Override
    public TypedProvider<IEntityType<?>> entityType() {
        return Registry.createEntityTypeRegistryFA();
    }

    @Override
    public TypedProvider<IEntityCapability<?>> entityCapability() {
        return Registry.createEntityCapabilityRegistryFA();
    }

    @Override
    public TypedProvider<EntityDataSerializer<?>> entityDataSerializer() {
        return Registry.createEntityDataSerializerRegistryFA();
    }

    @Override
    public TypedProvider<IMenuType<?>> menuType() {
        return Registry.createMenuTypeRegistryFA();
    }

    @Override
    public TypedProvider<SoundEvent> soundEvent() {
        return Registry.createSoundEventRegistryFA();
    }

    @Override
    public TypedProvider<IArgumentType<?>> commandArgumentType() {
        return TypedProvider.passthrough();
    }
}
