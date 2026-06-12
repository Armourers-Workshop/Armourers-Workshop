package moe.plushie.armourers_workshop.init.platform.forge.runtime;

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

public class CommonRegistryPlatformImpl implements CommonRegistryFactory {

    @Override
    public TypedProvider<IDataComponentType<?>> dataComponentType() {
        return Registry.createDataComponentTypeRegistryFO();
    }

    @Override
    public TypedProvider<IDataAttachmentType<?>> dataAttachmentType() {
        return TypedProvider.passthrough();
    }

    @Override
    public TypedProvider<Item> item() {
        return Registry.createItemRegistryFO();
    }

    @Override
    public TypedProvider<CreativeModeTab> creativeModeTab() {
        return Registry.createCreativeModeTabRegistryFO();
    }

    @Override
    public TypedProvider<ILootItemFunctionType<?>> lootItemFunctionType() {
        return Registry.createLootItemFunctionTypeRegistryFO();
    }

    @Override
    public TypedProvider<ITagKey<?>> itemTag() {
        return Registry.createItemTagRegistryFO();
    }

    @Override
    public TypedProvider<ITagKey<?>> blockTag() {
        return Registry.createBlockTagRegistryFO();
    }

    @Override
    public TypedProvider<Block> block() {
        return Registry.createBlockRegistryFO();
    }

    @Override
    public TypedProvider<IBlockEntityType<?>> blockEntityType() {
        return Registry.createBlockEntityTypeRegistryFO();
    }

    @Override
    public TypedProvider<IBlockEntityCapability<?>> blockEntityCapability() {
        return Registry.createBlockEntityCapabilityRegistryFO();
    }

    @Override
    public TypedProvider<IEntityType<?>> entityType() {
        return Registry.createEntityTypeRegistryFO();
    }

    @Override
    public TypedProvider<IEntityCapability<?>> entityCapability() {
        return Registry.createEntityCapabilityRegistryFO();
    }

    @Override
    public TypedProvider<EntityDataSerializer<?>> entityDataSerializer() {
        return Registry.createEntityDataSerializerRegistryFO();
    }

    @Override
    public TypedProvider<IMenuType<?>> menuType() {
        return Registry.createMenuTypeRegistryFO();
    }

    @Override
    public TypedProvider<SoundEvent> soundEvent() {
        return Registry.createSoundEventRegistryFO();
    }

    @Override
    public TypedProvider<IArgumentType<?>> commandArgumentType() {
        return TypedProvider.passthrough();
    }
}
