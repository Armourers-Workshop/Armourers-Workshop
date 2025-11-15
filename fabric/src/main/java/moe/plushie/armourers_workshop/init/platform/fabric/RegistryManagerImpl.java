package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.common.ITagKey;
import moe.plushie.armourers_workshop.api.common.ILootItemFunctionType;
import moe.plushie.armourers_workshop.api.common.IMenuType;
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
        return Registry.createDataComponentTypeRegistryFA();
    }

    @Override
    public TypedProvider<IDataAttachmentType<?>> dataAttachmentTypes() {
        return TypedProvider.passthrough(); // not support in the fabric.
    }

    @Override
    public TypedProvider<Item> items() {
        return Registry.createItemRegistryFA();
    }

    @Override
    public TypedProvider<CreativeModeTab> creativeModeTabs() {
        return Registry.createCreativeModeTabRegistryFA();
    }

    @Override
    public TypedProvider<ILootItemFunctionType<?>> lootItemFunctions() {
        return Registry.createLootItemFunctionTypeRegistryFA();
    }

    @Override
    public TypedProvider<ITagKey<?>> itemTags() {
        return Registry.createItemTagRegistryFA();
    }

    @Override
    public TypedProvider<ITagKey<?>> blockTags() {
        return Registry.createBlockTagRegistryFA();
    }

    @Override
    public TypedProvider<Block> blocks() {
        return Registry.createBlockRegistryFA();
    }

    @Override
    public TypedProvider<IBlockEntityType<?>> blockEntityTypes() {
        return Registry.createBlockEntityTypeRegistryFA();
    }

    @Override
    public TypedProvider<IBlockEntityCapability<?>> blockEntityCapabilities() {
        return Registry.createBlockEntityCapabilityRegistryFA();
    }

    @Override
    public TypedProvider<IEntityType<?>> entityTypes() {
        return Registry.createEntityTypeRegistryFA();
    }

    @Override
    public TypedProvider<IEntityCapability<?>> entityCapabilities() {
        return Registry.createEntityCapabilityRegistryFA();
    }

    @Override
    public TypedProvider<EntityDataSerializer<?>> entitySerializers() {
        return Registry.createEntityDataSerializerRegistryFA();
    }

    @Override
    public TypedProvider<IMenuType<?>> menuTypes() {
        return Registry.createMenuTypeRegistryFA();
    }

    @Override
    public TypedProvider<SoundEvent> soundEvents() {
        return Registry.createSoundEventRegistryFA();
    }

    @Override
    public TypedProvider<IArgumentType<?>> commandArgumentTypes() {
        return TypedProvider.passthrough();
    }
}
