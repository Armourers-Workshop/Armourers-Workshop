package moe.plushie.armourers_workshop.init.platform.forge;

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
import moe.plushie.armourers_workshop.init.platform.RegistryManager;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("unused")
public class RegistryManagerImpl extends RegistryManager {

    @Override
    public TypedProvider<IDataComponentType<?>> dataComponentTypes() {
        return Registry.createDataComponentTypeRegistryFO();
    }

    @Override
    public TypedProvider<IDataAttachmentType<?>> dataAttachmentTypes() {
        return TypedProvider.passthrough();
    }

    @Override
    public TypedProvider<Item> items() {
        return Registry.createItemRegistryFO();
    }

    @Override
    public TypedProvider<CreativeModeTab> creativeModeTabs() {
        return Registry.createCreativeModeTabRegistryFO();
    }

    @Override
    public TypedProvider<ILootItemFunctionType<?>> lootItemFunctions() {
        return Registry.createLootItemFunctionTypeRegistryFO();
    }

    @Override
    public TypedProvider<ITagKey<?>> itemTags() {
        return Registry.createItemTagRegistryFO();
    }

    @Override
    public TypedProvider<ITagKey<?>> blockTags() {
        return Registry.createBlockTagRegistryFO();
    }

    @Override
    public TypedProvider<Block> blocks() {
        return Registry.createBlockRegistryFO();
    }

    @Override
    public TypedProvider<IBlockEntityType<?>> blockEntityTypes() {
        return Registry.createBlockEntityTypeRegistryFO();
    }

    @Override
    public TypedProvider<IBlockEntityCapability<?>> blockEntityCapabilities() {
        return Registry.createBlockEntityCapabilityRegistryFO();
    }

    @Override
    public TypedProvider<IEntityType<?>> entityTypes() {
        return Registry.createEntityTypeRegistryFO();
    }

    @Override
    public TypedProvider<IEntityCapability<?>> entityCapabilities() {
        return Registry.createEntityCapabilityRegistryFO();
    }

    @Override
    public TypedProvider<EntityDataSerializer<?>> entitySerializers() {
        return Registry.createEntityDataSerializerRegistryFO();
    }

    @Override
    public TypedProvider<IMenuType<?>> menuTypes() {
        return Registry.createMenuTypeRegistryFO();
    }

    @Override
    public TypedProvider<SoundEvent> soundEvents() {
        return Registry.createSoundEventRegistryFO();
    }

    @Override
    public TypedProvider<IArgumentType<?>> commandArgumentTypes() {
        return TypedProvider.passthrough();
    }
}
