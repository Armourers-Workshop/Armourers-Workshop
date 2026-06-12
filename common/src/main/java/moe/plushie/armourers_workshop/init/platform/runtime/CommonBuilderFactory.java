package moe.plushie.armourers_workshop.init.platform.runtime;

import moe.plushie.armourers_workshop.api.client.key.IKeyCategory;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.common.IEntityDataSerializer;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.common.ILootItemFunction;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.api.registry.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IBlockBuilder;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.ICreativeModeTabBuilder;
import moe.plushie.armourers_workshop.api.registry.IDataComponentTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.api.registry.IEntitySerializerBuilder;
import moe.plushie.armourers_workshop.api.registry.IEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IItemBuilder;
import moe.plushie.armourers_workshop.api.registry.IKeyCategoryBuilder;
import moe.plushie.armourers_workshop.api.registry.IKeyMappingBuilder;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IPermissionNodeBuilder;
import moe.plushie.armourers_workshop.api.registry.ISoundEventBuilder;
import moe.plushie.armourers_workshop.api.registry.ITagKeyBuilder;
import moe.plushie.armourers_workshop.compat.api.AbstractBlockMaterial;
import moe.plushie.armourers_workshop.compat.api.AbstractBlockMaterialColor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Optional;
import java.util.function.Function;

public interface CommonBuilderFactory {

    <T extends Item> IItemBuilder<T> item(Function<Item.Properties, T> supplier);

    <T extends Item> ITagKeyBuilder<T> itemTag();

    <T extends CreativeModeTab> ICreativeModeTabBuilder<T> creativeModeTab();

    <T extends Block> IBlockBuilder<T> block(Function<BlockBehaviour.Properties, T> supplier, AbstractBlockMaterial material, AbstractBlockMaterialColor materialColor);

    <T extends BlockEntity> IBlockEntityTypeBuilder<T> blockEntityType(IBlockEntityType.Serializer<T> serializer);

    <T extends Entity> IEntityTypeBuilder<T> entityType(IEntityType.Serializer<T> serializer, MobCategory mobCategory);

    <T> IEntitySerializerBuilder<T> entitySerializer(IEntityDataSerializer<T> serializer);

    <T extends AbstractContainerMenu, V> IMenuTypeBuilder<T> menuType(IMenuProvider<T, V> factory, IMenuSerializer<V> serializer);

    <T extends IArgumentType<?>> IArgumentTypeBuilder<T> argumentType(IArgumentSerializer<T> serializer);

    <T> IEntityCapabilityBuilder<T> entityCapability(Class<T> type, Function<Entity, Optional<T>> factory);

    <T> IBlockEntityCapabilityBuilder<T> blockEntityCapability(Class<T> type, Function<Entity, Optional<T>> factory);

    <T extends IKeyCategory> IKeyCategoryBuilder<T> keyCategory();

    <T extends IKeyMapping> IKeyMappingBuilder<T> keyMapping(String key, IKeyCategory category);

    <T extends ILootItemFunction> ILootFunctionTypeBuilder<T> lootFunctionType(IDataMapCodec<T> codec);

    <T extends IPermissionNode> IPermissionNodeBuilder<T> permission();

    <T> IDataComponentTypeBuilder<T> dataComponentType(IDataCodec<T> codec);

    <T extends SoundEvent> ISoundEventBuilder<T> soundEvent();
}
