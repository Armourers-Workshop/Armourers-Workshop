package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.api.client.key.IKeyBinding;
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
import moe.plushie.armourers_workshop.api.registry.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IPermissionNodeBuilder;
import moe.plushie.armourers_workshop.api.registry.ISoundEventBuilder;
import moe.plushie.armourers_workshop.api.registry.ITagKeyBuilder;
import moe.plushie.armourers_workshop.compat.api.AbstractBlockMaterial;
import moe.plushie.armourers_workshop.compat.api.AbstractBlockMaterialColor;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ArgumentTypeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.BlockBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.BlockEntityCapabilityBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.BlockEntityTypeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.CreativeModeTabBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.DataComponentTypeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.EntityCapabilityBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.EntitySerializerBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.EntityTypeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ItemBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ItemTagBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.KeyBindingBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.LootFunctionTypeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.MenuTypeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.PermissionNodeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.SoundEventBuilderImpl;
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

@SuppressWarnings("unused")
public class BuilderManagerImpl extends BuilderManager {

    @Override
    public <T extends Item> IItemBuilder<T> createItemBuilder(Function<Item.Properties, T> supplier) {
        return new ItemBuilderImpl<>(supplier);
    }

    @Override
    public <T extends Item> ITagKeyBuilder<T> createItemTagBuilder() {
        return new ItemTagBuilderImpl<>();
    }

    @Override
    public <T extends CreativeModeTab> ICreativeModeTabBuilder<T> createItemGroupBuilder() {
        return new CreativeModeTabBuilderImpl<>();
    }

    @Override
    public <T extends Block> IBlockBuilder<T> createBlockBuilder(Function<BlockBehaviour.Properties, T> supplier, AbstractBlockMaterial material, AbstractBlockMaterialColor materialColor) {
        return new BlockBuilderImpl<>(supplier, material, materialColor);
    }

    @Override
    public <T extends BlockEntity> IBlockEntityTypeBuilder<T> createBlockEntityTypeBuilder(IBlockEntityType.Serializer<T> serializer) {
        return new BlockEntityTypeBuilderImpl<>(serializer);
    }

    @Override
    public <T extends Entity> IEntityTypeBuilder<T> createEntityTypeBuilder(IEntityType.Serializer<T> serializer, MobCategory mobCategory) {
        return new EntityTypeBuilderImpl<>(serializer, mobCategory);
    }

    @Override
    public <T> IEntitySerializerBuilder<T> createEntitySerializerBuilder(IEntityDataSerializer<T> serializer) {
        return new EntitySerializerBuilderImpl<>(serializer);
    }

    @Override
    public <T extends AbstractContainerMenu, D> IMenuTypeBuilder<T> createMenuTypeBuilder(IMenuProvider<T, D> factory, IMenuSerializer<D> serializer) {
        return new MenuTypeBuilderImpl<>(factory, serializer);
    }

    @Override
    public <T extends IArgumentType<?>> IArgumentTypeBuilder<T> createArgumentTypeBuilder(IArgumentSerializer<T> serializer) {
        return new ArgumentTypeBuilderImpl<>(serializer);
    }

    @Override
    public <T> IEntityCapabilityBuilder<T> createEntityCapabilityBuilder(Class<T> type, Function<Entity, Optional<T>> factory) {
        return new EntityCapabilityBuilderImpl<>(type, factory);
    }

    @Override
    public <T> IBlockEntityCapabilityBuilder<T> createBlockEntityCapabilityBuilder(Class<T> type, Function<Entity, Optional<T>> factory) {
        return new BlockEntityCapabilityBuilderImpl<>(type, factory);
    }

    @Override
    public <T extends IKeyBinding> IKeyBindingBuilder<T> createKeyBindingBuilder(String key) {
        return new KeyBindingBuilderImpl<>(key);
    }

    @Override
    public <T extends ILootItemFunction> ILootFunctionTypeBuilder<T> createLootFunctionTypeBuilder(IDataMapCodec<T> codec) {
        return new LootFunctionTypeBuilderImpl<>(codec);
    }

    @Override
    public <T extends IPermissionNode> IPermissionNodeBuilder<T> createPermissionBuilder() {
        return new PermissionNodeBuilderImpl<>();
    }

    @Override
    public <T> IDataComponentTypeBuilder<T> createDataComponentTypeBuilder(IDataCodec<T> codec) {
        return new DataComponentTypeBuilderImpl<>(codec);
    }

    @Override
    public <T extends SoundEvent> ISoundEventBuilder<T> createSoundEventBuilder() {
        return new SoundEventBuilderImpl<>();
    }
}
